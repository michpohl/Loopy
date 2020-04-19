//
// Created by Michael on 2019-07-13.
//
#include <string.h>
#include <jni.h>
#include <oboe/Oboe.h>
#include "AudioEngine.h"
#include "utils/logging.h"
#include <android/asset_manager_jni.h>
#include <memory>
#include "ObserverChain.h"

extern "C" {

std::unique_ptr<AudioEngine> audioEngine;
std::unique_ptr<AudioCallback> callback;

JavaVM *g_jvm = nullptr;
static jobject gClassLoader;
static jmethodID gFindClassMethod;

static jobject myJNIClass;

static jclass myClass;

JNIEnv* getEnv() {
    JNIEnv *env;
    int status = g_jvm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if(status < 0) {
        status = g_jvm->AttachCurrentThread(&env, NULL);
        if(status < 0) {
            return nullptr;
        }
    }
    return env;
}

jclass findClass(const char* name) {
    return static_cast<jclass>(getEnv()->CallObjectMethod(gClassLoader, gFindClassMethod, getEnv()->NewStringUTF(name)));
}

jint JNI_OnLoad(JavaVM *pJvm, void *reserved) {
    g_jvm = pJvm;


//    auto env = getEnv();
//

//
//    auto randomClass = env->FindClass("de/michaelpohl/loopy/common/jni/JniBridge");
//    jclass classClass = env->GetObjectClass(randomClass);
//    auto classLoaderClass = env->FindClass("java/lang/ClassLoader");
//    auto getClassLoaderMethod = env->GetMethodID(classClass, "getClassLoader",
//                                                 "()Ljava/lang/ClassLoader;");
//
//
//
//    gClassLoader = env->NewGlobalRef(env->CallObjectMethod(randomClass, getClassLoaderMethod));
//    gFindClassMethod = env->GetMethodID(classLoaderClass, "findClass",
//                                        "(Ljava/lang/String;)Ljava/lang/Class;");
//
//    // global classref approach
//
//    jobject tmp = env->FindClass("de/michaelpohl/loopy/common/jni/JniBridge");
////    jmethodID id = env->GetMethodID(tmp, "integerCallback", "(I)V");
//    myJNIClass = env->NewGlobalRef(tmp);
////    myMethod = env->NewGlobalRef(id);
//

    return JNI_VERSION_1_6;
}



JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_playFromJNI(JNIEnv *env, jobject instance,jstring URI) {
    LOGD("PlayFromJNI");
    myJNIClass = env->NewGlobalRef(instance);
//    myClass = env->FindClass("de/michaelpohl/loopy/common/jni/JniBridge");
//    jmethodID id = env->GetMethodID(myClass, "integerCallback", "(I)V");
//    env->CallVoidMethod(instance, id, (jint) 101);

    callback = std::make_unique<AudioCallback>(*g_jvm, myJNIClass);
    callback->playBackProgress(104);

    const char *uri = env->GetStringUTFChars(URI, NULL);
//    std::string s(uri);

    AMediaExtractor *extractor = AMediaExtractor_new();
    if (extractor == nullptr) {
        LOGE("Could not obtain AMediaExtractor");
        return;
    }
    media_status_t amresult = AMediaExtractor_setDataSource(extractor, uri);
    if (amresult != AMEDIA_OK) {
        LOGE("Error setting extractor data source, err %d", amresult);
    }
    audioEngine = std::make_unique<AudioEngine>(*extractor, *callback);
    audioEngine->setFileName(uri);
    audioEngine->start();

}

} // extern "C"
extern "C"
JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_stopJNIPlayback(JNIEnv *env, jobject instance) {
    audioEngine->stop();
}

extern "C"
JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_test(JNIEnv *env, jobject instance) {

    // Construct a String
    jstring jstr = env->NewStringUTF("This string comes from JNI");
    // First get the class that contains the method you need to call
    jclass clazz = env->FindClass("de/michaelpohl/loopy/common/jni/JniBridge");
    // Get the method that you want to call
    jmethodID messageMe = env->GetMethodID(clazz, "testCallBack", "(Ljava/lang/String;)V");
    // Call the method on the object
    env->CallVoidMethod(instance, messageMe, jstr);    // Get a C-style string

}

//FROM HERE CALLBACK STUFF

#include <vector>


std::vector<ObserverChain *> store_Wlistener_vector;

JNIEnv *store_env;


void txtCallback(JNIEnv *env, const _jstring *message_);

//extern "C"
//JNIEXPORT jstring JNICALL
//Java_de_michaelpohl_loopy_common_jni_JniBridge_stringFromJNI(
//        JNIEnv *env,
//        jobject /* this */) {
//
//    return env->NewStringUTF(hello.c_str());
//}

extern "C"
JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_nsubscribeListener(JNIEnv *env, jobject instance,
                                                                  jobject listener) {

    env->GetJavaVM(&g_jvm); //store jvm reference for later call

    store_env = env;

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass clazz = env->GetObjectClass(store_Wlistener);

    jmethodID store_method = env->GetMethodID(clazz, "onAcceptMessage", "(Ljava/lang/String;)V");
    jmethodID store_methodVAL = env->GetMethodID(clazz, "onAcceptMessageVal", "(I)V");

    ObserverChain *tmpt = new ObserverChain(store_Wlistener, store_method, store_methodVAL);

    store_Wlistener_vector.push_back(tmpt);


    __android_log_print(ANDROID_LOG_VERBOSE, "GetEnv:", " Subscribe to Listener  OK \n");
    if (NULL == store_method) return;


}
extern "C"
JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_ndismissListener(JNIEnv *env, jobject instance) {
    if (!store_Wlistener_vector.empty()) {
        for (int i = 0; i < store_Wlistener_vector.size(); i++) {
            env->DeleteWeakGlobalRef(store_Wlistener_vector[i]->store_Wlistener);
            store_Wlistener_vector[i]->store_method = NULL;
            store_Wlistener_vector[i]->store_methodVAL = NULL;
        }
        store_Wlistener_vector.clear();
    }


}

void test_string_callback_fom_c(char *val) {
    LOGD("GetEnv: start Callback  to JNL [%s]", val);
    JNIEnv *g_env;
    if (NULL == g_jvm) {
        LOGD("GetEnv:  No VM ");
        return;
    }
    //  double check it's all ok
    JavaVMAttachArgs args;
    args.version = JNI_VERSION_1_6; // set your JNI version
    args.name = NULL; // you might want to give the java thread a name
    args.group = NULL; // you might want to assign the java thread to a ThreadGroup

    int getEnvStat = g_jvm->GetEnv((void **) &g_env, JNI_VERSION_1_6);

    if (getEnvStat == JNI_EDETACHED) {
        LOGD("GetEnv: not attached");
        if (g_jvm->AttachCurrentThread(&g_env, &args) != 0) {
            LOGD("GetEnv: Failed to attach");
        }
    } else if (getEnvStat == JNI_OK) {
        LOGD("GetEnv: JNI_OK");
    } else if (getEnvStat == JNI_EVERSION) {
        LOGD("GetEnv: version not supported");
    }

    jstring message = g_env->NewStringUTF(val);//

    txtCallback(g_env, message);

    if (g_env->ExceptionCheck()) {
        g_env->ExceptionDescribe();
    }

    if (getEnvStat == JNI_EDETACHED) {
        g_jvm->DetachCurrentThread();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_de_michaelpohl_loopy_common_jni_JniBridge_nonNextListener(JNIEnv *env, jobject instance,
                                                               jstring message_) {

    txtCallback(env, message_);

}

void txtCallback(JNIEnv *env, const _jstring *message_) {
    if (!store_Wlistener_vector.empty()) {
        for (int i = 0; i < store_Wlistener_vector.size(); i++) {
            env->CallVoidMethod(store_Wlistener_vector[i]->store_Wlistener,
                                store_Wlistener_vector[i]->store_method, message_);
        }

    }
}
