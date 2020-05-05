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


AudioEngine::AudioEngine(AudioCallback &callback) : mCallback(callback) {

}

bool isWaitMode = true;
std::atomic<AudioEngineState> mAudioEngineState{AudioEngineState::Loading};


void AudioEngine::setWaitMode(bool value) {
    isWaitMode = value;
}

bool AudioEngine::getWaitMode() {
    return isWaitMode;
}

AudioEngineState AudioEngine::getState() {
    return mAudioEngineState;
};


void AudioEngine::prepare() {

    if (!openStream()) {
        mAudioEngineState = AudioEngineState::FailedToLoad;
        return;
    }
    audioProperties = AudioProperties{
            .channelCount = mAudioStream->getChannelCount(),
            .sampleRate = mAudioStream->getSampleRate()
    };
    isPrepared = true;
}

void AudioEngine::start() {
    std::async(&AudioEngine::startPlaying, this);
}

void AudioEngine::startPlaying() {
    if (mAudioStream == nullptr) {
        LOGE("Cannot start playback: Audiostream is a null pointer.");
        return;
    }
    if (players.front() == nullptr) {
        LOGE("Cannot start playback: Track to play is a null pointer.");
        return;
    } else {
        LOGD("Play the first in the players vector!");
        players.front()->setPlaying(true);
    }
    mMixer.addTrack(players.front().get());
    Result result = mAudioStream->requestStart();
    if (result != Result::OK) {
        LOGE("Failed to start stream. Error: %s", convertToText(result));
        mAudioEngineState = AudioEngineState::FailedToLoad;
        return;
    }
    mAudioEngineState = AudioEngineState::Playing;
}

void AudioEngine::stop() {

    if (mAudioStream != nullptr) {
        mAudioStream->close();
        delete mAudioStream;
        mAudioStream = nullptr;
    }
    if (loopA != nullptr) {
        loopA->resetPlayHead();
    }
}

void AudioEngine::pause() {
    LOGD("Trying to pause");
    if (mAudioStream != nullptr) {
        mAudioStream->pause();
    }
    mAudioEngineState = AudioEngineState::Paused;
}

bool AudioEngine::prepareNextPlayer(const char *fileName, AMediaExtractor &extractor) {
    if (!isPrepared) {
        prepare();
    } else {
        LOGD("Engine is already prepared. Skipping...");
    }

    LOGD("Creating new player");
    std::unique_ptr<Player> newPlayer = std::make_unique<Player>(fileName, mCallback, extractor,
                                                                 audioProperties, std::bind(
                    &AudioEngine::onPlayerEnded, this));
    if (newPlayer == nullptr) {
        LOGE("Failed to create a player for file: %s", fileName);
        return false;
    }
    newPlayer->setLooping(true);
    if (!players.empty()) {
        players.front()->setLooping(false);
    }

    // adding the new player to the vector
    players.push_back(std::move(newPlayer));
    LOGD("Next player successfully prepared!");
    return true;
}

DataCallbackResult
AudioEngine::onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) {

    // If our audio stream is expecting 16-bit samples we need to render our floats into a separate
    // buffer then convert them into 16-bit ints
    bool is16Bit = (oboeStream->getFormat() == AudioFormat::I16);
    float *outputBuffer = (is16Bit) ? mConversionBuffer.get() : static_cast<float *>(audioData);

    for (int i = 0; i < numFrames; ++i) {

        // mixer disabled because we only play files consecutively
        // mMixer.renderAudio(outputBuffer + (oboeStream->getChannelCount() * i), 1);

        players.front()->renderAudio(outputBuffer + (oboeStream->getChannelCount() * i), 1);
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


void AudioEngine::onPlayerEnded() {
    LOGD("Player ended");
    players.back()->setPlaying(true);
    players.erase(players.begin());
    LOGD("Player erased");
    if (!players.empty()) {

    }

}
