//
// Created by Michael on 14.03.20.
//

#ifndef LOOPY_AUDIOCALLBACK_H
#define LOOPY_AUDIOCALLBACK_H


#include <jni.h>

class AudioCallback {

public:
   explicit AudioCallback(JNIEnv&, jobject);
    void playBackProgress(int progressPercentage);

private:
    JNIEnv& mEnv;
    jobject& mObject;
};


#endif //LOOPY_AUDIOCALLBACK_H
