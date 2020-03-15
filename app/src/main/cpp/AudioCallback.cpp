//
// Created by Michael on 14.03.20.
//

#include <jni.h>>
#include "AudioCallback.h"

AudioCallback::AudioCallback() {
}

void AudioCallback::playBackProgress(int progressPercentage) {
    mEnv->CallVoidMethod(mObject, mId, (jint)progressPercentage);
}

void AudioCallback::setMethodId(jmethodID id) {
    mId = id;
}

void AudioCallback::setEnv(JNIEnv *env) {
    mEnv = env;
}

void AudioCallback::setJObject(jobject object) {
    mObject = object;
}

