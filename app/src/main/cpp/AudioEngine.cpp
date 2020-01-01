//
// Created by Michael on 2019-07-19.
//

#include "Constants.h"
#include "AAssetDataSource.h"
#include <thread>
#include <cinttypes>
#include "AudioEngine.h"
#include "logging.h"
#include <fstream>

AudioEngine::AudioEngine() {
}

void AudioEngine::prepare(std::string fileName) {
    fileToPlay = fileName;
    LOGD("received this filename: %s", fileToPlay.c_str());
}

void AudioEngine::start() {

    // async returns a future, we must store this future to avoid blocking. It's not sufficient
    // to store this in a local variable as its destructor will block until Game::load completes.
    mLoadingResult = std::async(&AudioEngine::load, this);

}

void AudioEngine::load() {

    if (!openStream()) {
        mGameState = GameState::FailedToLoad;
        return;
    }

    if (!setupSource()) {
        mGameState = GameState::FailedToLoad;
        return;
    }

    Result result = mAudioStream->requestStart();
    if (result != Result::OK) {
        LOGE("Failed to start stream. Error: %s", convertToText(result));
        mGameState = GameState::FailedToLoad;
        return;
    }

    mGameState = GameState::Playing;
}

bool AudioEngine::setupSource() {
    LOGD("Loop source: %s", fileToPlay.c_str());

    //  Set the properties of our audio source(s) to match that of our audio stream
    AudioProperties targetProperties{
            .channelCount = mAudioStream->getChannelCount(),
            .sampleRate = mAudioStream->getSampleRate()
    };


//    // Create a data source and player
//    const char *x = fileToPlay.c_str();
//    std::shared_ptr<AAssetDataSource> loopSource{
//            AAssetDataSource::newFromCompressedAsset(mAssetManager, x, targetProperties)
//    };

//    if (loopSource == nullptr) {
//        LOGE("Could not load source data for backing track");
//        return false;
//    }
//    loop = std::make_unique<Player>(loopSource);
//    loop->setPlaying(true);
//    loop->setLooping(true);
//
//    mMixer.addTrack(loop.get());

    return true;
}

void AudioEngine::playFile(const char *filename) {
    std::lock_guard<std::mutex> lock(mDataLock);

    if (mData != nullptr) {
        delete mData;
        mData = nullptr;
    }

    std::ifstream file(filename, std::ifstream::in | std::ifstream::binary);
    LOGD("file state: %i, fail: %i, bad: %i", file.good(), file.fail(), file.bad());
    if (file.is_open()) {
        // Parse header
        int32_t length = 0;
        if (!parseWave(file, &length)) {
            LOGE("Failed to parse WAVE file.");

            // Fallback?
            file.seekg(0, file.end);
            length = file.tellg();
            file.seekg(0, file.beg);
        }

        int samples = (length) / 2;
        int16_t *data = new int16_t[samples];
        int index = 0;
        char buffer[2];
        while (!file.eof() && index < samples) {
            file.read(buffer, 2);
            data[index++] = *reinterpret_cast<int16_t *>(buffer);
        }

        // There are 4 bytes per frame because
        // each sample is 2 bytes and
        // it's a stereo recording which has 2 samples per frame.
        mTotalFrames = (int32_t) (samples / 2);
        mReadFrameIndex = 0;

        mData = data;
        LOGD("length: %i, samples: %i, mTotalFrames: %i, index: %i", length, samples, mTotalFrames,
             index);

        file.close();
    } else {
        LOGE("could not open file: %s", filename);
    }
}


bool AudioEngine::openStream() {

    // Create an audio stream
    AudioStreamBuilder builder;
    builder.setCallback(this);
    builder.setPerformanceMode(PerformanceMode::LowLatency);
    builder.setSharingMode(SharingMode::Exclusive);

    Result result = builder.openStream(&mAudioStream);
    if (result != Result::OK) {
        LOGE("Failed to open stream. Error: %s", convertToText(result));
        return false;
    }

    if (mAudioStream->getFormat() == AudioFormat::I16) {
        mConversionBuffer = std::make_unique<float[]>(
                (size_t) mAudioStream->getBufferCapacityInFrames() *
                mAudioStream->getChannelCount());
    }
//
//    // Reduce stream latency by setting the buffer size to a multiple of the burst size
    auto setBufferSizeResult = mAudioStream->setBufferSizeInFrames(
            mAudioStream->getFramesPerBurst() *
            2); // Use 2 bursts as the buffer size (double buffer)
    if (setBufferSizeResult != Result::OK) {
        LOGW("Failed to set buffer size. Error: %s", convertToText(setBufferSizeResult.error()));
    }

    mMixer.setChannelCount(mAudioStream->getChannelCount());

    return true;
}

bool AudioEngine::parseWave(std::ifstream &file, int32_t *length)
{
    char buffer[4];
    file.read(buffer, 4);
    if (strncmp(buffer, "RIFF", 4) != 0)
        return false;

    file.seekg(8);
    file.read(buffer, 4);
    if (strncmp(buffer, "WAVE", 4) != 0)
        return false;

    // Find data segment
    int chuckPos = 12;
    while (file.good()) {
        file.read(buffer, 4);
        // TODO: Verify fmt chunk? should be PCM,16bit,2ch,44100kHz
        if (strncmp(buffer, "data", 4) == 0) {
            // FOUND IT!
            file.read(buffer, 4);
            *length = buffer[0] | (buffer[1] << 8) | (buffer[2] << 16) | (buffer[3] << 24);
            return true;
        }
        else {
            // different chunk
            file.read(buffer, 4);
            int32_t size = buffer[0] | (buffer[1] << 8) | (buffer[2] << 16) | (buffer[3] << 24);
            chuckPos += 8 + size;
            file.seekg(chuckPos);
        }
    }
    return false;
}

DataCallbackResult
AudioEngine::onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) {
    // If our audio stream is expecting 16-bit samples we need to render our floats into a separate
    // buffer then convert them into 16-bit ints
    bool is16Bit = (oboeStream->getFormat() == AudioFormat::I16);
    float *outputBuffer = (is16Bit) ? mConversionBuffer.get() : static_cast<float *>(audioData);

    for (int i = 0; i < numFrames; ++i) {

        mSongPositionMs = convertFramesToMillis(
                mCurrentFrame,
                mAudioStream->getSampleRate());

        mMixer.renderAudio(outputBuffer + (oboeStream->getChannelCount() * i), 1);
        mCurrentFrame++;
    }

    if (is16Bit) {
        oboe::convertFloatToPcm16(outputBuffer,
                                  static_cast<int16_t *>(audioData),
                                  numFrames * oboeStream->getChannelCount());
    }

    mLastUpdateTime = nowUptimeMillis();

    return DataCallbackResult::Continue;
}

void AudioEngine::onErrorAfterClose(AudioStream *oboeStream, Result error) {
    AudioStreamCallback::onErrorAfterClose(oboeStream, error);
}


