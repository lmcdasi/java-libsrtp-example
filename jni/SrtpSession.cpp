#include <iostream>
#include "SrtpSession.h"

JNIEXPORT jlong JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_createSrtpContext(JNIEnv *env, jclass obj,
                                                                                          jstring cipher) {
    const char* nativeString = env->GetStringUTFChars(cipher, 0);
    std::string cppString(nativeString);
    env->ReleaseStringUTFChars(cipher, nativeString);

    SrtpContext::Builder builder;
    builder.withCipher(cppString);

    SrtpContext* srtpContext = new SrtpContext(builder.build());
    return reinterpret_cast<jlong>(srtpContext);
}

JNIEXPORT void JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_destroySrtpContext(JNIEnv *env, jclass obj,
                                                                                          jlong srtpContextPtr) {
    SrtpContext* srtpContext = reinterpret_cast<SrtpContext*>(srtpContextPtr);
    delete srtpContext;
}

JNIEXPORT jlong JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_createSrtpSession(JNIEnv *env, jclass obj,
                                                                                          jlong srtpContextPtr) {
    SrtpContext* srtpContext = reinterpret_cast<SrtpContext*>(srtpContextPtr);

    srtp_t srtp_sender;
    srtp_policy_t srtpPolicy = srtpContext->getPolicy();
    srtp_create(&srtp_sender, &srtpPolicy);

    return reinterpret_cast<jlong>(srtp_sender);
}

JNIEXPORT void JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_destroySrtpSession(JNIEnv *env, jclass obj,
                                                                                          jlong srtpSessionPtr) {
    srtp_t srtp_session = reinterpret_cast<srtp_t>(srtpSessionPtr);
    srtp_dealloc(srtp_session);
}

JNIEXPORT jint JNICALL Java_org_lmcdasi_demo_srtp_jni_srtp_SrtpSession_srtpUnprotect(JNIEnv *env, jobject obj,
                                                                                     jlong srtpSessionPtr,
                                                                                     jobject rtpPacketData,
                                                                                     jintArray rtpPacketLength) {
    srtp_t srtp_session = reinterpret_cast<srtp_t>(srtpSessionPtr);

    uint8_t *data = (uint8_t *) env->GetDirectBufferAddress(rtpPacketData);
    if (data == NULL) {
        return -1;
    }

    jint *length = env->GetIntArrayElements(rtpPacketLength, NULL);
    if (length == NULL) {
        return -1;
    }

    srtp_err_status_t status;
    try {
        status = srtp_unprotect(srtp_session, data, (int *) length);
    } catch(...) {
        status = srtp_err_status_t::srtp_err_status_fail;
    }

    env->ReleaseIntArrayElements(rtpPacketLength, length, 0);
    return status;
}
