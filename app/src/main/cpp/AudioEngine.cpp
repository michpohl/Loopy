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

//#include "MultiChannelResampler.h"

#include "oboe/Oboe.h"
#include "AudioEngine.h"


AudioEngine::AudioEngine(AudioCallback &callback) : mCallback(callback) {

}

bool isWaitMode = true;
int mSampleRate = 44100;
std::atomic<AudioEngineState> mAudioEngineState{AudioEngineState::Loading};


bool AudioEngine::setWaitMode(bool value) {
    isWaitMode = value;
    return true;
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
    Player *player = players.front().get();
    mMixer.addTrack(player);
    const char *filename = player->getName();
    Result result = mAudioStream->requestStart();
    if (result != Result::OK) {
        LOGE("Failed to start stream. Error: %s", convertToText(result));
        mAudioEngineState = AudioEngineState::FailedToLoad;
        return;
    }
    LOGD("Playing: %s", filename);
    mCallback.onFileStartsPlaying(filename);
    mAudioEngineState = AudioEngineState::Playing;
}

bool AudioEngine::stop() {

    if (mAudioStream != nullptr) {
        mAudioStream->close();
        delete mAudioStream;
        mAudioStream = nullptr;
    }
    if (loopA != nullptr) {
        loopA->resetPlayHead();
    }
    isPrepared = false;
    players.clear();
    mAudioEngineState = AudioEngineState::Stopped;
    return true;
}

bool AudioEngine::pause() {
    LOGD("Trying to pause");
    if (mAudioStream != nullptr) {
        mAudioStream->requestPause();
    }
    mAudioEngineState = AudioEngineState::Paused;
    return true;
}

bool AudioEngine::resume() {
    LOGD("Trying to resume");
    if (mAudioStream != nullptr) {
        LOGD("Requesting start");
        mAudioStream->requestStart();
    }
    mAudioEngineState = AudioEngineState::Playing;
    return true;
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

    if (isWaitMode) {

        // removing the last player in the vector, since we are preselecting a different one
        if (players.size() > 1) {
            players.erase(players.begin() + players.size() - 1);
        }
    } else {
        // since we're not in wait mode, we just throw away all players and replace them with a new one
        stop();
        if (!isPrepared) {
            prepare();
        } else {
            LOGD("Engine is already prepared. Skipping...");
        }
        players.clear();
    }

    // adding the new player to the vector
    players.push_back(std::move(newPlayer));
    LOGD("Next player successfully prepared! Waiting for loop to end");
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
    builder.setSampleRate(mSampleRate);
    builder.setSampleRateConversionQuality(SampleRateConversionQuality::Best);

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
    LOGD("New stream open. Sample rate: %i", mAudioStream->getSampleRate());
    return true;
}

void AudioEngine::onPlayerEnded() {
    LOGD("Player ended");
    players.back()->setPlaying(true);
    const char *filename = players.back()->getName();
    mCallback.onFileStartsPlaying(filename);
    players.erase(players.begin());
    LOGD("Player erased");
    if (!players.empty()) {
    }
}

int AudioEngine::getSampleRate() {
    return mSampleRate;
}

bool AudioEngine::setSampleRate(int sampleRate) {
    mSampleRate = sampleRate;
    players.clear();
    LOGD("Clearing players...new size: %i", players.size());
    LOGD("Opening stream...");
    openStream();
    LOGD("Done");
    return true;
}
