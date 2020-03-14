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

extern "C" {

std::unique_ptr<AudioEngine> audioEngine;

std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes,
                                                                       env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte *pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *) pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_playFromJNI(JNIEnv *env, jobject instance,
                                                           jstring URI) {

    const char *uri = env->GetStringUTFChars(URI, NULL);
    std::string s(uri);

    AMediaExtractor *extractor = AMediaExtractor_new();
    if (extractor == nullptr) {
        LOGE("Could not obtain the AAssetManager");
        return;
    }
    media_status_t amresult = AMediaExtractor_setDataSource(extractor, uri);
    if (amresult != AMEDIA_OK) {
        LOGE("AMediaExtractor_setDataSource called with: [%s]", s.c_str());
        LOGE("Error setting extractor data source, err %d", amresult);
    } else {
        LOGD("Extracting seems ok, %d", amresult);
    }
    audioEngine = std::make_unique<AudioEngine>(*extractor);
    audioEngine->setFileName(uri);
    audioEngine->start();
}

//    JNIEXPORT void JNICALL
//    Java_de_michaelpohl_loopy_common_jni_JniBridge_playFromJNI2(JNIEnv *env, jobject jinstance,
//                                                                jstring filePath) {
//        LOGD("Trying to play from storage");
//
//        std::string convertedFileName = jstring2string(env, filePath);
//        audioEngine = std::make_unique<AudioEngine>();
////    audioEngine->playFile(convertedFileName);
//        audioEngine->start();
//    }

} // extern "C"