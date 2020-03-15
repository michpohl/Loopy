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

JNIEnv * mEnv;
jobject mInstance;

JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_playFromJNI(JNIEnv *env, jobject instance,
                                                           jstring URI) {

    // storing env & instance for later reuse
    mEnv = env;
    mInstance = instance;

    const char *uri = env->GetStringUTFChars(URI, NULL);
    std::string s(uri);

    AMediaExtractor *extractor = AMediaExtractor_new();
    if (extractor == nullptr) {
        LOGE("Could not obtain AMediaExtractor");
        return;
    }
    media_status_t amresult = AMediaExtractor_setDataSource(extractor, uri);
    if (amresult != AMEDIA_OK) {
        LOGE("Error setting extractor data source, err %d", amresult);
    }
    audioEngine = std::make_unique<AudioEngine>(*extractor);
    audioEngine->setFileName(uri);
    audioEngine->start();
}

} // extern "C"
extern "C"
JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_stopJNIPlayback(JNIEnv *env, jobject instance) {
    audioEngine->stop();
}

extern "C"
JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_test(JNIEnv *env, jobject instance) {

    // Construct a String
    jstring jstr = env->NewStringUTF("This string comes from JNI");
    // First get the class that contains the method you need to call
    jclass clazz = env->FindClass("de/michaelpohl/loopy/common/jni/JniBridge");
    // Get the method that you want to call
    jmethodID messageMe = env->GetMethodID(clazz, "testCallBack", "(Ljava/lang/String;)V");
    // Call the method on the object
    env->CallVoidMethod(instance, messageMe, jstr);    // Get a C-style string

}