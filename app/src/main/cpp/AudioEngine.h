//
// Created by Michael on 2019-07-19.
//

#ifndef OBOE_TEST_AUDIOENGINE_H
#define OBOE_TEST_AUDIOENGINE_H

#include <future>

#include <android/asset_manager.h>

#include <oboe/Oboe.h>
#include "Player.h"
#include "AAssetDataSource.h"
#include "OpenGLFunctions.h"
#include "Mixer.h"
#include "Constants.h"
#include "UtilityFunctions.h"

using namespace oboe;

enum class GameState {
    Loading,
    Playing,
    FailedToLoad
};

class AudioEngine : public AudioStreamCallback {

public:
    explicit AudioEngine();

    void prepare(std::string fileName);

    void start();

    // Inherited from oboe::AudioStreamCallback
    DataCallbackResult
    onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) override;

    void onErrorAfterClose(AudioStream *oboeStream, Result error) override;
    void playFile(const char * filename);

private:
    AudioStream *mAudioStream{nullptr};
    std::string fileToPlay;
    std::unique_ptr<Player> loop;
    Mixer mMixer;
    std::unique_ptr<float[]> mConversionBuffer{nullptr}; // For float->int16 conversion
    std::atomic<int64_t> mCurrentFrame{0};
    std::atomic<int64_t> mSongPositionMs{0};
    std::atomic<int64_t> mLastUpdateTime{0};
    std::atomic<GameState> mGameState{GameState::Loading};
    std::future<void> mLoadingResult;


    void load();

    bool openStream();

    bool setupSource();

    // added from that library
    oboe::AudioApi mAudioApi = oboe::AudioApi::Unspecified;
    int32_t mPlaybackDeviceId = oboe::kUnspecified;

    oboe::AudioStream *mPlayStream;
    std::mutex mRestartingLock;

    std::mutex mDataLock;

    int32_t mChannelCount;
    int32_t mFramesPerBurst;

    // Audio file params:
    int32_t mReadFrameIndex = 0;
    const int16_t* mData = nullptr;
    int32_t mTotalFrames = 0;

//    void createPlaybackStream();
//
//    void closeOutputStream();
//
//    void restartStream();
//
//    void setupPlaybackStreamParameters(oboe::AudioStreamBuilder *builder);
//    bool parseWave(std::ifstream &file, int32_t *length);

    bool parseWave(std::ifstream &file, int32_t *length);
};

#endif //OBOE_TEST_AUDIOENGINE_H
