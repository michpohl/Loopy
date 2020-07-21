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

#ifndef OBOE_TEST_SOUNDRECORDING_H
#define OBOE_TEST_SOUNDRECORDING_H

#include <cstdint>
#include <array>

#include <chrono>
#include <memory>
#include <atomic>

#include <android/asset_manager.h>
#include <media/NdkMediaExtractor.h>

#include "IRenderableAudio.h"
#include "StorageDataSource.h"
#include "AudioCallback.h"
#include <vector>

class Player : public IRenderableAudio {

public:
    /**
     * Construct a new Player from the given DataSource. Players can share the same data source.
     * For example, you could play two identical sounds concurrently by creating 2 Players with the
     * same data source.
     *
     * @param source
     */

    typedef std::function<void()> PlaybackEndedCallback;
    PlaybackEndedCallback playbackEndedCallback;

    Player(const char *fileName, AudioCallback &callback, AMediaExtractor &extractor,
           AudioProperties properties, PlaybackEndedCallback c);

    bool isPlaying() { return mIsPlaying; };

    const char *getName() { return mFilename; };

    void renderAudio(float *targetData, int32_t numFrames);

    void resetPlayHead() {
        mReadFrameIndex = 0;
        position = 0;
    };

    void setPlaying(bool isPlaying) {
        mIsPlaying = isPlaying;
        resetPlayHead();
    };

    void setLooping(bool isLooping) { mIsLooping = isLooping; };

private:
    const char *mFilename;
    float position = 0;
    double lastProgressCall = 0;
    int32_t mReadFrameIndex = 0;
    std::atomic<bool> mIsPlaying{false};
    std::atomic<bool> mIsLooping{false};
    std::unique_ptr <StorageDataSource> mSource;
    AudioCallback &mCallback;

    void renderSilence(float *, int32_t);

    double now_ms(void);
};

#endif //OBOE_TEST_SOUNDRECORDING_H
