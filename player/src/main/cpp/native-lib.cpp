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
#include "Converter.h"


extern "C" {

std::unique_ptr<Converter> converter;
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
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_selectNative(JNIEnv *env, jobject instance,
                                                            jstring URI) {
    LOGD("loadNative");


    const char *uri = env->GetStringUTFChars(URI, NULL);
    AMediaExtractor *extractor = AMediaExtractor_new();

    if (audioEngineExists(env, instance)) {
        return audioEngine->prepareNextPlayer(uri, *extractor);
    }
    return false;
}

JNIEXPORT void JNICALL
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_startPlaybackNative(JNIEnv *env, jobject instance) {
    if (audioEngineExists(env, instance)) {
        if (!audioEngine->getWaitMode() || audioEngine->getState() != AudioEngineState::Playing) {
            audioEngine->start();
        } else {
            LOGD("Playback not started on click, because WAIT mode is on and the engine is playing right now");
        }
    }
}

JNIEXPORT jboolean JNICALL
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_stopPlaybackNative(JNIEnv *env,
                                                                  jobject instance) {

    if (audioEngineExists(env, instance)) {
        bool success = audioEngine->stop();
        return (jboolean) success;
    }
    return (jboolean) false;
}

JNIEXPORT jboolean JNICALL
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_pausePlaybackNative(JNIEnv *env, jobject instance) {
    if (audioEngineExists(env, instance)) {
        bool success = audioEngine->pause();
        return (jboolean) success;
    }
    return (jboolean) false;
}

JNIEXPORT jboolean JNICALL
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_resumePlaybackNative(JNIEnv *env, jobject instance) {
    if (audioEngineExists(env, instance)) {
        bool success = audioEngine->resume();
        return (jboolean) success;
    }
    return (jboolean) false;
}

}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_setWaitModeNative(JNIEnv *env, jobject instance,
                                                                 jboolean should_wait) {
    if (audioEngineExists(env, instance)) {
        if (audioEngine->setWaitMode((bool) should_wait)) {
            return (jboolean) true;
        }
    }
    return jboolean(false);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_convertFolder(JNIEnv *env, jobject instance,
                                                             jstring folder_name) {

    if (callback == nullptr) {
        myJNIClass = env->NewGlobalRef(instance);
        callback = std::make_unique<AudioCallback>(*g_jvm, myJNIClass);
    }

    if (converter == nullptr) converter = std::__ndk1::make_unique<Converter>(*callback);
    const char *folder = env->GetStringUTFChars(folder_name, nullptr);

    if (converter->setDestinationFolder(folder)) {
        LOGD("Set folder name to: %s", folder);
        converter->convertFolder();
    }
    return false;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_convertSingleFile(JNIEnv *env, jobject instance,
                                                                 jstring file_name,
                                                                 jstring file_path,
                                                                 jstring set_path) {

    if (callback == nullptr) {
        myJNIClass = env->NewGlobalRef(instance);
        callback = std::make_unique<AudioCallback>(*g_jvm, myJNIClass);
    }

    if (converter == nullptr) converter = std::__ndk1::make_unique<Converter>(*callback);
    const char *folder = env->GetStringUTFChars(set_path, nullptr);
    const char *path = env->GetStringUTFChars(file_path, nullptr);
    const char *name = env->GetStringUTFChars(file_name, nullptr);

    if (converter->setDestinationFolder(folder)) {
        LOGD("Set folder name to: %s", folder);
        bool result = converter->convertSingleFile(path, name);
        return result;
    }
    return false;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_michaelpohl_loopyplayer2_common_jni_JniBridge_setSampleRateNative(JNIEnv *env, jobject instance, jint sampleRate) {
    if (audioEngineExists(env, instance)) {
        int currentSampleRate = audioEngine->getSampleRate();
        if (currentSampleRate != (int) sampleRate) {
            LOGD("Sample rate is different (engine: %i, desired: %i). Changing...", currentSampleRate, sampleRate);
        bool result = audioEngine->setSampleRate((int)sampleRate);
        return result;
        } else {
            return true;
        }
    }
    return false;
}
