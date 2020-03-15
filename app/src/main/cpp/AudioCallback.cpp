//
// Created by Michael on 14.03.20.
//

#include <jni.h>>
#include <utils/logging.h>
#include "AudioCallback.h"

AudioCallback::AudioCallback(JNIEnv &env, jobject object) : mEnv(env), mObject(object) {
}

static JavaVM *jvm = NULL;

// TODO looks like this class won't need the env at all. Can be improved
void AudioCallback::playBackProgress(int progressPercentage) {


    JNIEnv *g_env = &mEnv;
    mEnv.GetJavaVM(&jvm); //store jvm reference for later call

    if (jvm != NULL) {

        int getEnvStat = jvm->GetEnv((void **) g_env, JNI_VERSION_1_6);
        JavaVMAttachArgs vmAttachArgs;
        vmAttachArgs.version = JNI_VERSION_1_6;
        vmAttachArgs.name = NULL;
        vmAttachArgs.group = NULL;

        if (getEnvStat == JNI_EDETACHED) {
            LOGD("GetEnv: not attached - attaching");
            if (jvm->AttachCurrentThread(&g_env, &vmAttachArgs) != 0) {
                LOGD("GetEnv: Failed to attach");
            }
        } else if (getEnvStat == JNI_OK) {
            LOGD("GetEnv: JNI_OK");
        } else if (getEnvStat == JNI_EVERSION) {
            LOGD("GetEnv: version not supported");
        }
        LOGD("Env stat: %d", getEnvStat);
    }
    LOGD("Progress! %d", progressPercentage);
//    jclass target = mEnv->FindClass("de/michaelpohl/loopy/common/jni/JniBridge");
//    jmethodID  id = mEnv->GetMethodID(target, "integerCallback", "(I)V");
//    mEnv->CallVoidMethod(mObject, mId, (jint)progressPercentage);
}


