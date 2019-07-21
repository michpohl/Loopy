//
// Created by Michael on 2019-07-19.
//

#include "Constants.h"
#include "AAssetDataSource.h"
#include <thread>
#include <cinttypes>
#include "AudioEngine.h"
#include "logging.h"

AudioEngine::AudioEngine(AAssetManager &assetManager): mAssetManager(assetManager) {
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
    if (result != Result::OK){
        LOGE("Failed to start stream. Error: %s", convertToText(result));
        mGameState = GameState::FailedToLoad;
        return;
    }

    mGameState = GameState::Playing;
}

bool AudioEngine::setupSource() {

    //  Set the properties of our audio source(s) to match that of our audio stream
    AudioProperties targetProperties{
            .channelCount = mAudioStream->getChannelCount(),
            .sampleRate = mAudioStream->getSampleRate()
    };


    // Create a data source and player
    std::shared_ptr<AAssetDataSource> loopSource{
            AAssetDataSource::newFromCompressedAsset(mAssetManager, loopFileName, targetProperties)
    };
    if (loopSource == nullptr) {
        LOGE("Could not load source data for backing track");
        return false;
    }
    loop = std::make_unique<Player>(loopSource);
    loop->setPlaying(true);
    loop->setLooping(true);

    mMixer.addTrack(loop.get());

    return true;
}


bool AudioEngine::openStream() {

    // Create an audio stream
    AudioStreamBuilder builder;
    builder.setCallback(this);
    builder.setPerformanceMode(PerformanceMode::LowLatency);
    builder.setSharingMode(SharingMode::Exclusive);

    Result result = builder.openStream(&mAudioStream);
    if (result != Result::OK){
        LOGE("Failed to open stream. Error: %s", convertToText(result));
        return false;
    }

    if (mAudioStream->getFormat() == AudioFormat::I16){
        mConversionBuffer = std::make_unique<float[]>(
                (size_t)mAudioStream->getBufferCapacityInFrames() *
                mAudioStream->getChannelCount());
    }
//
//    // Reduce stream latency by setting the buffer size to a multiple of the burst size
    auto setBufferSizeResult = mAudioStream->setBufferSizeInFrames(
            mAudioStream->getFramesPerBurst() * 2); // Use 2 bursts as the buffer size (double buffer)
    if (setBufferSizeResult != Result::OK){
        LOGW("Failed to set buffer size. Error: %s", convertToText(setBufferSizeResult.error()));
    }

    mMixer.setChannelCount(mAudioStream->getChannelCount());

    return true;
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

        mMixer.renderAudio(outputBuffer+(oboeStream->getChannelCount()*i), 1);
        mCurrentFrame++;
    }

    if (is16Bit){
        oboe::convertFloatToPcm16(outputBuffer,
                                  static_cast<int16_t*>(audioData),
                                  numFrames * oboeStream->getChannelCount());
    }

    mLastUpdateTime = nowUptimeMillis();

    return DataCallbackResult::Continue;
}

void AudioEngine::onErrorAfterClose(AudioStream *oboeStream, Result error) {
    AudioStreamCallback::onErrorAfterClose(oboeStream, error);
}


