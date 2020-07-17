//
// Created by Michael on 2019-07-13.
//
#include <string.h>
#include <jni.h>
#include <oboe/Oboe.h>
#include "AudioEngine.h"
#include "utils/logging.h"
#include <android/asset_manager_jni.h>
#include <memory>
#include "ObserverChain.h"

extern "C" {

std::unique_ptr<AudioEngine> audioEngine;
std::unique_ptr<AudioCallback> callback;

JavaVM *g_jvm = nullptr;

static jobject myJNIClass;


jint JNI_OnLoad(JavaVM *pJvm, void *reserved) {
    g_jvm = pJvm;
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_selectNative(JNIEnv *env, jobject instance,
                                                            jstring URI, jboolean isWaitMode) {
    LOGD("loadNative");
    myJNIClass = env->NewGlobalRef(instance);

    if (callback == nullptr) {
        callback = std::make_unique<AudioCallback>(*g_jvm, myJNIClass);
    }

    const char *uri = env->GetStringUTFChars(URI, NULL);
    AMediaExtractor *extractor = AMediaExtractor_new();
    if (extractor == nullptr) {
        LOGE("Could not obtain AMediaExtractor");
        return;
    }
    media_status_t amresult = AMediaExtractor_setDataSource(extractor, uri);
    if (amresult != AMEDIA_OK) {
        LOGE("Error setting extractor data source, err %d", amresult);
    }
    if (audioEngine == nullptr) {
        LOGD("AudioEngine created");
        audioEngine = std::make_unique<AudioEngine>(*callback);
    } else {
        LOGD("Not instantiation audioEngine, we already have one.");
    }
    audioEngine->setWaitMode((bool) isWaitMode);
    audioEngine->prepareNextPlayer(uri, *extractor);
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_startPlaybackNative(JNIEnv *env, jobject thiz) {
    if (audioEngine == nullptr) {
        LOGE(" Cannot start playback: AudioEngine is null!");
        return;
    }
    if (!audioEngine->getWaitMode() || audioEngine->getState() != AudioEngineState::Playing) {
        audioEngine->start();
    } else {
        LOGD("Playback not started on click, because WAIT mode is on and the engine is playing right now");
    }
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_stopPlaybackNative(JNIEnv *env, jobject instance) {

    if (audioEngine != nullptr) {
        audioEngine->stop();
    }
}

JNIEXPORT jboolean JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_pausePlaybackNative(JNIEnv *env, jobject thiz) {
//TODO this boolean value does not yet really tell if everything worked
    if (audioEngine != nullptr) {
        audioEngine->pause();
        return (jboolean) true;
    }
    return (jboolean) false;
}
}
