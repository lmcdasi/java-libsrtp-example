#include "jni.h"
#include "srtp.h"

#ifndef _Included_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent
#define _Included_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpInit(JNIEnv *, jclass);
JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpInstallLogHandler(JNIEnv *, jobject, jobject, jobject);
JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpSetDebugModule(JNIEnv *, jobject, jstring, jint);
JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent_srtpShutdown(JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
