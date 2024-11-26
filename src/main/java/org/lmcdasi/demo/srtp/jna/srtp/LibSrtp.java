package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.lmcdasi.demo.srtp.common.SrtpErrStatus;
import org.lmcdasi.demo.srtp.common.SrtpLogLevelT;

public interface LibSrtp extends Library {
    SrtpErrStatus srtp_init();
    SrtpErrStatus srtp_add_stream(Pointer session, SrtpPolicyT policy);
    SrtpErrStatus srtp_dealloc(Pointer session);
    SrtpErrStatus srtp_get_stream_roc(Pointer session, long ssrc, IntByReference roc);
    SrtpErrStatus srtp_set_stream_roc(Pointer session, long ssrc, long roc);
    SrtpErrStatus srtp_create(PointerByReference session, SrtpPolicyT policy);
    SrtpErrStatus srtp_set_debug_module(String name, int v);
    SrtpErrStatus srtp_shutdown();
    SrtpErrStatus srtp_unprotect(Pointer session, Pointer srtpHdr, IntByReference lenPtr);

    void srtp_crypto_policy_set_aes_cm_128_hmac_sha1_32(SrtpCryptoPolicyT srtpCryptoPolicyPointer);
    void srtp_crypto_policy_set_rtp_default(SrtpCryptoPolicyT srtpCryptoPolicyPointer);
    void srtp_crypto_policy_set_aes_cm_256_hmac_sha1_32(SrtpCryptoPolicyT srtpCryptoPolicyPointer);
    void srtp_crypto_policy_set_aes_cm_256_hmac_sha1_80(SrtpCryptoPolicyT srtpCryptoPolicyPointer);

    SrtpErrStatus srtp_install_log_handler(SrtpLogHandlerFuncT func, Pointer data);

    interface SrtpLogHandlerFuncT extends Callback {
        void callback(SrtpLogLevelT level, String msg, Pointer data);
    }
}
