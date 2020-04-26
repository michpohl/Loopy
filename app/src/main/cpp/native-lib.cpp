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
Java_de_michaelpohl_loopy_common_jni_JniBridge_loadNative(JNIEnv *env, jobject instance,
                                                          jstring URI) {
    LOGD("loadNative");
    myJNIClass = env->NewGlobalRef(instance);

    callback = std::make_unique<AudioCallback>(*g_jvm, myJNIClass);
    callback->playBackProgress(104);

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
    audioEngine = std::make_unique<AudioEngine>(*extractor, *callback);
    audioEngine->loadFile(uri);
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_startPlaybackNative(JNIEnv *env, jobject thiz) {
    if (audioEngine != nullptr) {
        audioEngine->start();
    } else {
        LOGE("AudioEngine is null!");
    }
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_stopPlaybackNative(JNIEnv *env, jobject instance) {

    if (audioEngine != nullptr) {
        audioEngine->stop();
    }
}

}
