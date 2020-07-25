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
    LOGD("OnLoad");
    g_jvm = pJvm;

    return JNI_VERSION_1_6;
}

bool audioEngineExists(JNIEnv *env, jobject instance) {

    if (callback == nullptr) {
        myJNIClass = env->NewGlobalRef(instance);
        callback = std::make_unique<AudioCallback>(*g_jvm, myJNIClass);
    }

    if (audioEngine == nullptr) {
        if (g_jvm == nullptr) LOGD("No jvm");

        audioEngine = std::__ndk1::make_unique<AudioEngine>(*callback);
        if (audioEngine != nullptr) {
            LOGD("AudioEngine created");
            return true;
        }
    } else if (audioEngine != nullptr) {
        LOGD("Not instantiating audioEngine, we already have one.");
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_selectNative(JNIEnv *env, jobject instance,
                                                            jstring URI) {
    LOGD("loadNative");


    const char *uri = env->GetStringUTFChars(URI, NULL);
    AMediaExtractor *extractor = AMediaExtractor_new();
    if (extractor == nullptr) {
        LOGE("Could not obtain AMediaExtractor");
        return false;
    }
    media_status_t amresult = AMediaExtractor_setDataSource(extractor, uri);
    if (amresult != AMEDIA_OK) {
        LOGE("Error setting extractor data source, err %d", amresult);
    }
    if (audioEngineExists(env, instance)) {
        return audioEngine->prepareNextPlayer(uri, *extractor);
    }
    return false;
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_startPlaybackNative(JNIEnv *env, jobject instance) {
    if (audioEngineExists(env, instance)) {
        if (!audioEngine->getWaitMode() || audioEngine->getState() != AudioEngineState::Playing) {
            audioEngine->start();
        } else {
            LOGD("Playback not started on click, because WAIT mode is on and the engine is playing right now");
        }
    }
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_stopPlaybackNative(JNIEnv *env,
                                                                  jobject instance) {

    if (audioEngineExists(env, instance)) {
        audioEngine->stop();
    }
}

JNIEXPORT jboolean JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_pausePlaybackNative(JNIEnv *env, jobject instance) {
//TODO this boolean value does not yet really tell if everything worked
    if (audioEngineExists(env, instance)) {
        audioEngine->pause();
        return (jboolean) true;
    }
    return (jboolean) false;
}
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_setWaitModeNative(JNIEnv *env, jobject instance,
                                                                 jboolean should_wait) {
    if (audioEngineExists(env, instance)) {
        if (audioEngine->setWaitMode((bool) should_wait)) {
            return (jboolean) true;
        }
    }
    return jboolean(false);
}
