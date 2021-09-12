//
// Created by Michael on 14.03.20.
//

#ifndef LOOPY_AUDIOCALLBACK_H
#define LOOPY_AUDIOCALLBACK_H


#include <jni.h>

class AudioCallback {

public:
    explicit AudioCallback(JavaVM &, jobject);

    void updatePlaybackProgress(const char *filename, int progressPercentage);

    void onFileStartsPlaying(const char *fileName);

    void updateConversionProgress(const char *filename, int steps);

    void onFilePreselected(const char *fileName);

private:
    JavaVM &g_jvm;
    jobject g_object;

    JNIEnv *getEnv(JNIEnv *&g_env) const;

};


#endif //LOOPY_AUDIOCALLBACK_H
