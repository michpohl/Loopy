//
// Created by Michael on 14.03.20.
//

#include <jni.h>>
#include <utils/logging.h>
#include "AudioCallback.h"

AudioCallback::AudioCallback(JavaVM &jvm, jobject &object) : mJvm(jvm), mObject(object) {
}

void AudioCallback::playBackProgress(int progressPercentage) {


    JNIEnv *g_env = NULL;

    int getEnvStat = mJvm.GetEnv((void **) &g_env, JNI_VERSION_1_6);
    JavaVMAttachArgs vmAttachArgs;
    vmAttachArgs.version = JNI_VERSION_1_6;
    vmAttachArgs.name = NULL;
    vmAttachArgs.group = NULL;

    if (getEnvStat == JNI_EDETACHED) {
        LOGD("GetEnv: not attached - attaching");
        if (mJvm.AttachCurrentThread(&g_env, &vmAttachArgs) != 0) {
            LOGD("GetEnv: Failed to attach");
        }
    } else if (getEnvStat == JNI_OK) {
        LOGD("GetEnv: JNI_OK");
    } else if (getEnvStat == JNI_EVERSION) {
        LOGD("GetEnv: version not supported");
    }
    LOGD("Env stat: %d", getEnvStat);

    LOGD("Progress! %d", progressPercentage);

    if (g_env != NULL) {
        jclass target = g_env->FindClass("de/michaelpohl/loopy/common/jni/JniBridge");
        jmethodID id = g_env->GetMethodID(target, "integerCallback", "(I)V");
        g_env->CallVoidMethod(mObject, id, (jint) progressPercentage);
    } else {
        LOGE("IT's null!");
//    mJvm.DetachCurrentThread();
    }
}


