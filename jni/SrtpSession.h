#include "jni.h"
#include "srtp.h"
#include "SrtpContext.h"

#ifndef _Included_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent
#define _Included_org_lmcdasi_demo_srtp_jni_srtp_SrtpComponent
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_createSrtpContext(JNIEnv *, jclass, jstring);
JNIEXPORT void JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_destroySrtpContext(JNIEnv *, jclass, jlong);
JNIEXPORT jlong JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_createSrtpSession(JNIEnv *, jclass, jlong);
JNIEXPORT void JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_destroySrtpSession(JNIEnv *, jclass, jlong);
JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_srtpUnprotect(JNIEnv *, jobject, jlong, jobject, jintArray);

#ifdef __cplusplus
}
#endif
#endif