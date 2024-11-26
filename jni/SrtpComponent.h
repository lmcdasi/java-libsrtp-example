#include "jni.h"
#include "srtp.h"

#ifndef _Included_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent
#define _Included_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpInit(JNIEnv *, jclass);
//JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtp_install_log_handler(JNIEnv *, jobject, jobject, jobject);
//JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtp_set_debug_module(JNIEnv *env, jobject obj, jstring moduleName, jint level);
JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpShutdown(JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
