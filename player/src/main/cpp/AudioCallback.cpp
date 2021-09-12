//
// Created by Michael on 14.03.20.
//

#include <jni.h>>
#include <utils/logging.h>
#include <string>
#include "AudioCallback.h"


jmethodID progressChangedMethod;
jmethodID fileStartedMethod;
jmethodID filePreselectedMethod;
jmethodID conversionProgressMethod;
const char *mFileName;

AudioCallback::AudioCallback(JavaVM &jvm, jobject object) : g_jvm(jvm), g_object(object) {
    JNIEnv *g_env;
    int getEnvStat = g_jvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);
    LOGD("Env Stat: %d", getEnvStat);

    if (g_env != NULL) {
        jclass target = g_env->GetObjectClass(g_object);
        progressChangedMethod = g_env->GetMethodID(target, "onPlaybackProgressChanged",
                                                   "(Ljava/lang/String;I)V");
        fileStartedMethod = g_env->GetMethodID(target, "onStarted",
                                               "(Ljava/lang/String;)V");
        filePreselectedMethod = g_env->GetMethodID(target, "onSelected",
                                                   "(Ljava/lang/String;)V");
        conversionProgressMethod = g_env->GetMethodID(target, "onConversionProgressChanged",
                                                      "(Ljava/lang/String;I)V");
    }
}

void AudioCallback::onFileStartsPlaying(const char *fileName) {
    JNIEnv *g_env;
    g_env = getEnv(g_env);
    if (mFileName != fileName || mFileName == NULL) {
        mFileName = fileName;
        jstring callbackString = g_env->NewStringUTF(mFileName);
        g_env->CallVoidMethod(g_object, fileStartedMethod, callbackString);
        g_env->DeleteLocalRef(callbackString);
//    g_jvm.DetachCurrentThread();
        LOGD("end");
    }
}

JNIEnv *AudioCallback::getEnv(JNIEnv *&g_env) const {
    int getEnvStat = g_jvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        LOGD("GetEnv: not attached - attaching");
        int result = g_jvm.AttachCurrentThread(reinterpret_cast<JNIEnv **>(&g_env), NULL);
        if (result != 0) {
            LOGD("GetEnv: Failed to attach");
        }
    } else if (getEnvStat == JNI_EVERSION) {
        LOGD("GetEnv: version not supported");
    }
    return g_env;
}

void AudioCallback::onFilePreselected(const char *fileName) {
    if (mFileName != fileName || mFileName == NULL) {
        mFileName = fileName;

        JNIEnv *g_env;
        getEnv(g_env);

        jstring callbackString = g_env->NewStringUTF(mFileName);
        g_env->CallVoidMethod(g_object, filePreselectedMethod, callbackString);
        g_env->DeleteLocalRef(callbackString);
        LOGD("Done preselected");
//    g_jvm.DetachCurrentThread();
    }
}

void AudioCallback::updatePlaybackProgress(const char *filename, int progressPercentage) {
    JNIEnv *g_env;
    getEnv(g_env);

    jstring callbackString = g_env->NewStringUTF(filename);
    g_env->CallVoidMethod(g_object, progressChangedMethod, callbackString,
                          (jint) progressPercentage);
    g_env->DeleteLocalRef(callbackString);
//    mJvm.DetachCurrentThread();
}

void AudioCallback::updateConversionProgress(const char* filename, int steps) {
    JNIEnv *g_env;
    getEnv(g_env);
    jstring callbackString = g_env->NewStringUTF(filename);
    g_env->CallVoidMethod(g_object, conversionProgressMethod, callbackString, (jint) steps);
    g_env->DeleteLocalRef(callbackString);
}


