//
// Created by Michael on 14.03.20.
//

#include <jni.h>>
#include <utils/logging.h>
#include "AudioCallback.h"

jclass target;
jmethodID id;

AudioCallback::AudioCallback(JavaVM &jvm, jobject object) : g_jvm(jvm), g_object(object) {
    JNIEnv *g_env;
    int getEnvStat = g_jvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);
    LOGD("Env Stat: %d", getEnvStat);

    if (g_env != NULL) {
        target = g_env->GetObjectClass(g_object);
        id = g_env->GetMethodID(target, "integerCallback", "(I)V");
    }
}

void AudioCallback::playBackProgress(int progressPercentage) {
    JNIEnv *g_env;
    int getEnvStat = g_jvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);

    if (getEnvStat == JNI_EDETACHED) {
//        LOGD("GetEnv: not attached - attaching");
        if (g_jvm.AttachCurrentThread(&g_env, NULL) != 0) {
            LOGD("GetEnv: Failed to attach");
        }
    } else if (getEnvStat == JNI_OK) {
//        LOGD("GetEnv: JNI_OK");
    } else if (getEnvStat == JNI_EVERSION) {
//        LOGD("GetEnv: version not supported");
    }
    g_env->CallVoidMethod(g_object, id, (jint) progressPercentage);
//    mJvm.DetachCurrentThread();
}


