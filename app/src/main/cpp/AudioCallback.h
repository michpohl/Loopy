//
// Created by Michael on 14.03.20.
//

#ifndef LOOPY_AUDIOCALLBACK_H
#define LOOPY_AUDIOCALLBACK_H


#include <jni.h>

class AudioCallback {

public:
   explicit AudioCallback(JavaVM&, jobject);
    void playBackProgress(int progressPercentage);

private:
    JavaVM& g_jvm;
    jobject g_object;
};


#endif //LOOPY_AUDIOCALLBACK_H
