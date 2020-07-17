//
// Created by Michael on 14.03.20.
//

#include <jni.h>>
#include <utils/logging.h>
#include "AudioCallback.h"


jmethodID progressChangedMethod;
jmethodID fileNameChangedMethod;
jmethodID filePreselectedMethod;
const char *mFileName;

AudioCallback::AudioCallback(JavaVM &jvm, jobject object) : g_jvm(jvm), g_object(object) {
    JNIEnv *g_env;
    int getEnvStat = g_jvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);
    LOGD("Env Stat: %d", getEnvStat);

    if (g_env != NULL) {
        jclass target = g_env->GetObjectClass(g_object);
        progressChangedMethod = g_env->GetMethodID(target, "onPlaybackProgressChanged",
                                                   "(I)V");
        fileNameChangedMethod = g_env->GetMethodID(target, "onFileSelected",
                                                   "(Ljava/lang/String;)V");
        filePreselectedMethod = g_env->GetMethodID(target, "onSelected",
                                                   "(Ljava/lang/String;)V");
    }
}

void AudioCallback::onFileStartsPlaying(const char *fileName) {
    if (mFileName != fileName || mFileName == NULL) {
        mFileName = fileName;
        JNIEnv *g_env;
        int getEnvStat = g_jvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);

        if (getEnvStat == JNI_EDETACHED) {
            LOGD("GetEnv: not attached - attaching");
            if (g_jvm.AttachCurrentThread(&g_env, NULL) != 0) {
                LOGD("GetEnv: Failed to attach");
            }
        } else if (getEnvStat == JNI_OK) {
//            LOGD("GetEnv: JNI_OK");
        } else if (getEnvStat == JNI_EVERSION) {
            LOGD("GetEnv: version not supported");
        }
        jstring callbackString = g_env->NewStringUTF(mFileName);
        g_env->CallVoidMethod(g_object, fileNameChangedMethod, callbackString);
        g_env->DeleteLocalRef(callbackString);
//    mJvm.DetachCurrentThread();
    }
}

void AudioCallback::onFilePreselected(const char *fileName) {
    if (mFileName != fileName || mFileName == NULL) {
        mFileName = fileName;
        JNIEnv *g_env;
        int getEnvStat = g_jvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);
        // TODO there is duplicated code here. Maybe refactor
        if (getEnvStat == JNI_EDETACHED) {
            LOGD("GetEnv: not attached - attaching");
            if (g_jvm.AttachCurrentThread(&g_env, NULL) != 0) {
                LOGD("GetEnv: Failed to attach");
            }
        } else if (getEnvStat == JNI_OK) {
//            LOGD("GetEnv: JNI_OK");
        } else if (getEnvStat == JNI_EVERSION) {
            LOGD("GetEnv: version not supported");
        }
        jstring callbackString = g_env->NewStringUTF(mFileName);
        g_env->CallVoidMethod(g_object, filePreselectedMethod, callbackString);
        g_env->DeleteLocalRef(callbackString);
//    mJvm.DetachCurrentThread();
    }
}

void AudioCallback::updatePlaybackProgress(int progressPercentage) {
    JNIEnv *g_env;
    int getEnvStat = g_jvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);

    if (getEnvStat == JNI_EDETACHED) {
        LOGD("GetEnv: not attached - attaching");
        if (g_jvm.AttachCurrentThread(&g_env, NULL) != 0) {
            LOGD("GetEnv: Failed to attach");
        }
    } else if (getEnvStat == JNI_OK) {
//        LOGD("GetEnv: JNI_OK");
    } else if (getEnvStat == JNI_EVERSION) {
        LOGD("GetEnv: version not supported");
    }
    g_env->CallVoidMethod(g_object, progressChangedMethod, (jint) progressPercentage);
//    mJvm.DetachCurrentThread();
}


