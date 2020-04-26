/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <utils/logging.h>
#include <inttypes.h>

#include "oboe/Oboe.h"
#include "AudioEngine.h"


AudioEngine::AudioEngine(AMediaExtractor &extractor, AudioCallback &callback)
        : mExtraxtor(
        extractor), mCallback(callback) {
}

const char *mFileName;


void AudioEngine::prepare() {

    if (!openStream()) {
        mAudioEngineState = AudioEngineState::FailedToLoad;
        return;
    }

    if (!setupAudioSources()) {
        mAudioEngineState = AudioEngineState::FailedToLoad;
        return;
    }
}

void AudioEngine::start() {
    std::async(&AudioEngine::startPlaying, this);
}

void AudioEngine::startPlaying() {
    if (mAudioStream == nullptr) {
        LOGE("Cannot start playback: Audiostream is a null pointer.");
        return;
    }
    if (mBackingTrack == nullptr) {
        LOGE("Cannot start playback: Track to play is a null pointer.");
        return;
    }
    Result result = mAudioStream->requestStart();
    if (result != Result::OK) {
        LOGE("Failed to start stream. Error: %s", convertToText(result));
        mAudioEngineState = AudioEngineState::FailedToLoad;
        return;
    }
    mAudioEngineState = AudioEngineState::Playing;
}

void AudioEngine::stop() {

    //also: differentiate between stop and pause...
    if (mAudioStream != nullptr) {
        mAudioStream->close();
        delete mAudioStream;
        mAudioStream = nullptr;
    }
    if (mBackingTrack != nullptr) {
        mBackingTrack->resetPlayHead();
    }
}

void AudioEngine::loadFile(const char *fileName) {
    mFileName = fileName;
    prepare();

}

DataCallbackResult
AudioEngine::onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) {

    // If our audio stream is expecting 16-bit samples we need to render our floats into a separate
    // buffer then convert them into 16-bit ints
    bool is16Bit = (oboeStream->getFormat() == AudioFormat::I16);
    float *outputBuffer = (is16Bit) ? mConversionBuffer.get() : static_cast<float *>(audioData);

    for (int i = 0; i < numFrames; ++i) {
        mMixer.renderAudio(outputBuffer + (oboeStream->getChannelCount() * i), 1);
        mCurrentFrame++;
    }

    if (is16Bit) {
        oboe::convertFloatToPcm16(outputBuffer,
                                  static_cast<int16_t *>(audioData),
                                  numFrames * oboeStream->getChannelCount());
    }

    mLastUpdateTime = nowUptimeMillis();
//    mCallback.playBackProgress((int) mLastUpdateTime);
    return DataCallbackResult::Continue;
}

void AudioEngine::onErrorAfterClose(AudioStream *oboeStream, Result error) {
    LOGE("The audio stream was closed, please restart the game. Error: %s", convertToText(error));
};

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

    // Reduce stream latency by setting the buffer size to a multiple of the burst size
    auto setBufferSizeResult = mAudioStream->setBufferSizeInFrames(
            mAudioStream->getFramesPerBurst() * kBufferSizeInBursts);
    if (setBufferSizeResult != Result::OK) {
        LOGW("Failed to set buffer size. Error: %s", convertToText(setBufferSizeResult.error()));
    }

    mMixer.setChannelCount(mAudioStream->getChannelCount());

    return true;
}

bool AudioEngine::setupAudioSources() {

    // Set the properties of our audio source(s) to match that of our audio stream
    AudioProperties targetProperties{
            .channelCount = mAudioStream->getChannelCount(),
            .sampleRate = mAudioStream->getSampleRate()
    };

    // Create a data source and player for our backing track
    std::shared_ptr<StorageDataSource> backingTrackSource{
            StorageDataSource::newFromStorageAsset(mExtraxtor, mFileName, targetProperties)
    };
    if (backingTrackSource == nullptr) {
        LOGE("Could not prepare source data for backing track");
        return false;
    }
    mBackingTrack = std::make_unique<Player>(backingTrackSource, mCallback);
    mBackingTrack->resetPlayHead();
    mBackingTrack->setPlaying(true);
    mBackingTrack->setLooping(true);

    // Add player to our mixer
    mMixer.addTrack(mBackingTrack.get());

    return true;
}

