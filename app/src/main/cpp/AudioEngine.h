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

#ifndef RHYTHMGAME_GAME_H
#define RHYTHMGAME_GAME_H

#include <future>

#include <android/asset_manager.h>
#include <oboe/Oboe.h>

#include "Mixer.h"

#include "Player.h"
#include "StorageDataSource.h"
#include "OpenGLFunctions.h"
#include "LockFreeQueue.h"
#include "utils/UtilityFunctions.h"
#include "Constants.h"
#include "AudioCallback.h"
#include <media/NdkMediaExtractor.h>


using namespace oboe;

enum class AudioEngineState {
    Loading,
    Playing,
    Paused,
    Stopped,
    FailedToLoad
};

class AudioEngine : public AudioStreamCallback {
public:
    explicit AudioEngine(AudioCallback &);

    void start();

    void startPlaying();

    bool stop();

    bool pause();

    bool resume();

    bool setWaitMode(bool value);
    bool getWaitMode();

    int getSampleRate();
    bool setSampleRate(int sampleRate);

    AudioEngineState getState();

    bool prepareNextPlayer(const char *fileName, AMediaExtractor &extractor);

    // Inherited from oboe::AudioStreamCallback
    DataCallbackResult
    onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) override;

    void onErrorAfterClose(AudioStream *oboeStream, Result error) override;

    void onPlayerEnded();

private:
    AudioCallback &mCallback;
    AudioStream *mAudioStream{nullptr};
    AudioProperties audioProperties;
    std::vector<std::unique_ptr<Player>> players;
    std::unique_ptr<Player> loopA;
    std::unique_ptr<Player> loopB;
    Mixer mMixer;
    std::unique_ptr<float[]> mConversionBuffer{nullptr}; // For float->int16 conversion

    LockFreeQueue<int64_t, kMaxQueueItems> mClapEvents;
    std::atomic<int64_t> mCurrentFrame{0};
    std::atomic<int64_t> mSongPositionMs{0};
    LockFreeQueue<int64_t, kMaxQueueItems> mClapWindows;
    LockFreeQueue<TapResult, kMaxQueueItems> mUiEvents;
    std::atomic<int64_t> mLastUpdateTime{0};
    std::future<void> mLoadingResult;

    void prepare();

    bool openStream();

    bool isPrepared = false;

};


#endif //RHYTHMGAME_GAME_H
