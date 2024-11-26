#include <iostream>
#include "SrtpComponent.h"

JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpInit(JNIEnv *env, jclass obj) {
    return srtp_init();
}

/**
JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtp_install_log_handler(JNIEnv *env,
                       jobject obj, jobject logCallback, jobject userData) {
    return srtp_install_log_handler(logCallback, userData);
}

JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtp_set_debug_module(JNIEnv *env,
                       jobject obj, jstring moduleName, jint level) {
    const char *module_name = (*env)->GetStringUTFChars(env, moduleName, NULL);
    const int status = srtp_set_debug_module(module_name, level);
    (*env)->ReleaseStringUTFChars(env, moduleName, module_name);
    return status;
}
*/

JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpShutdown(JNIEnv *env, jclass obj) {
    return srtp_shutdown();
}
