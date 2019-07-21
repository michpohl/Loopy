//
// Created by Michael on 2019-07-13.
//
#include <string.h>
#include <jni.h>
#include <oboe/Oboe.h>
#include "AudioEngine.h"
#include "logging.h"
#include <android/asset_manager_jni.h>
#include <memory>



extern "C" {

std::unique_ptr<AudioEngine> audioEngine;

JNIEXPORT jstring JNICALL
Java_com_michaelpohl_oboetest_CppAdapter_stringFromJNI(JNIEnv *env,
                                                       jobject jinstance) {
    return (env)->NewStringUTF("Hello from JNI !");
}

JNIEXPORT void JNICALL
Java_com_michaelpohl_oboetest_CppAdapter_playFromJNI(JNIEnv *env, jobject jinstance, jobject jAssetManager) {
    LOGD("Trying to play");
    AAssetManager *assetManager = AAssetManager_fromJava(env, jAssetManager);
    if (assetManager == nullptr) {
        LOGE("Could not obtain the AAssetManager");
        return;
    }

    audioEngine= std::make_unique<AudioEngine>(*assetManager);
    audioEngine->start();
}
} // extern "C"