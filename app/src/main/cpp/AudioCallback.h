//
// Created by Michael on 14.03.20.
//

#ifndef LOOPY_AUDIOCALLBACK_H
#define LOOPY_AUDIOCALLBACK_H


#include <jni.h>

class AudioCallback {

public:
   explicit AudioCallback();
    void setMethodId(jmethodID id);
    void setEnv(JNIEnv *env);
    void setJObject(jobject object);
    void playBackProgress(int progressPercentage);

private:
    JNIEnv *mEnv;
    jmethodID mId;
    jobject mObject;
};


#endif //LOOPY_AUDIOCALLBACK_H
