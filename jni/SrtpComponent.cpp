#include <iostream>
#include "SrtpComponent.h"

static JavaVM* javaVM = nullptr;
static jobject globalLogCallback = nullptr;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    javaVM = vm;

    return JNI_VERSION_1_8;
}

void nativeLogHandler(srtp_log_level_t level, const char* message, void* userData) {
    JNIEnv* env = nullptr;
    if (javaVM->AttachCurrentThread((void**)&env, nullptr) != 0) {
        return;
    }

    const jclass logClass = env->GetObjectClass(globalLogCallback);
    const jmethodID logMethod = env->GetMethodID(logClass, "callback", "(Lorg/lmcdasi/demo/srtp/jni/srtp/SrtpLogLevelT;Ljava/lang/String;Ljava/nio/ByteBuffer;)V");

    if (logMethod == nullptr) {
        javaVM->DetachCurrentThread();
        return; // Method not found
    }

    const jstring jMessage = env->NewStringUTF(message);
    const jobject jUserData = env->NewDirectByteBuffer(userData, sizeof(void*));

    env->CallVoidMethod(globalLogCallback, logMethod, (jint)level, jMessage, jUserData);

    env->DeleteLocalRef(jMessage);
    env->DeleteLocalRef(jUserData);

    javaVM->DetachCurrentThread();
}

jobject convertToJavaLogLevel(JNIEnv* env, srtp_log_level_t level) {
    const jclass logLevelClass = env->FindClass("org/lmcdasi/demo/srtp/jni/srtp/SrtpLogLevelT");
    jmethodID valueOfMethod = env->GetStaticMethodID(logLevelClass, "valueOf", "(I)Lorg/lmcdasi/demo/srtp/jni/srtp/SrtpLogLevelT;");
    return env->CallStaticObjectMethod(logLevelClass, valueOfMethod, level);
}

JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpInit(JNIEnv *env, jclass obj) {
    return srtp_init();
}


JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpInstallLogHandler(JNIEnv* env, jobject obj,
                                                                                               jobject logCallback, jobject userData) {
    if (globalLogCallback != nullptr) {
        env->DeleteGlobalRef(globalLogCallback);
    }
    globalLogCallback = env->NewGlobalRef(logCallback);

    return srtp_install_log_handler(nativeLogHandler, nullptr);
}


JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpSetDebugModule(JNIEnv *env,
                       jobject obj, jstring moduleName, jint level) {
    const char *module_name = env->GetStringUTFChars(moduleName, NULL);
    const int status = srtp_set_debug_module(module_name, level);
    env->ReleaseStringUTFChars(moduleName, module_name);
    return status;
}


JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpShutdown(JNIEnv *env, jclass obj) {
    return srtp_shutdown();
}
