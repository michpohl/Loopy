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

std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_playFromJNI(JNIEnv *env, jobject jinstance, jobject jAssetManager, jstring fileName) {
    LOGD("Trying to play");

    std::string convertedFileName = jstring2string(env, fileName);
    audioEngine= std::make_unique<AudioEngine>();
    audioEngine->prepare(convertedFileName);
    audioEngine->start();
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_playFromJNI2(JNIEnv *env, jobject jinstance, jstring filePath) {
    LOGD("Trying to play from storage");

    std::string convertedFileName = jstring2string(env, filePath);
    audioEngine= std::make_unique<AudioEngine>();
    audioEngine->playFile(convertedFileName);
    audioEngine->start();
}

} // extern "C"