package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Memory;
import com.sun.jna.ptr.PointerByReference;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.lmcdasi.demo.srtp.common.SrtpSecServ;
import org.lmcdasi.demo.srtp.common.SrtpSsrcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.lmcdasi.demo.srtp.common.SrtpUtility.*;

final class SrtpContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(SrtpContext.class);

    @Getter
    private final SrtpPolicyT srtpPolicyT;

    private SrtpContext(@Nonnull final SrtpPolicyT srtpPolicyT) {
        this.srtpPolicyT = srtpPolicyT;
    }

    public static final class Builder {
        private LibSrtp libSrtp;
        private String cipher;

        public @Nonnull Builder withLibSrtp(@Nonnull final LibSrtp libSrtp) {
            this.libSrtp = libSrtp;
            return this;
        }

        public @Nonnull Builder withCipher(@Nonnull final String cipher) {
            this.cipher = cipher;
            return this;
        }

        SrtpContext build() {
            final var cipherSplit = cipher.split(" ");
            final var srtpAuth = getSrtpAuth(cipherSplit);
            final var srtpCipher = getSrtpCipher(cipherSplit);

            final var rtpCryptoPolicy = getSrtpCryptoPolicyT(srtpAuth, srtpCipher);
            rtpCryptoPolicy.setSec_serv(SrtpSecServ.SEC_SERV_CONF_AND_AUTH.getValue());

            final var rtcpCryptoPolicy = new SrtpCryptoPolicyT();
            rtcpCryptoPolicy.setSec_serv(SrtpSecServ.SEC_SERV_NONE.getValue());

            final var srtpPolicy = new SrtpPolicyT();
            srtpPolicy.setAllow_repeat_tx(0);

            final var srtpKey = getDecodedSrtpKey(cipherSplit);
            final var srtpKeyNative = new Memory(srtpKey.length);
            srtpKeyNative.write(0, srtpKey, 0, srtpKey.length);
            srtpPolicy.setKey(srtpKeyNative);

            srtpPolicy.setRtp(rtpCryptoPolicy);
            srtpPolicy.setRtcp(rtcpCryptoPolicy);
            srtpPolicy.setSsrc(new SrtpSsrcT(SrtpSsrcType.SSRC_ANY_INBOUND, 0));
            //srtpPolicy.setWindow_size(applicationPropertiesSrtp.getWindowSize());

            final var session = new PointerByReference();
            final var srtpErrStatus = libSrtp.srtp_create(session, srtpPolicy);

            return new SrtpContext(srtpPolicy);
        }

        @SuppressWarnings("PMD.CyclomaticComplexity")
        private SrtpCryptoPolicyT getSrtpCryptoPolicyT(@Nonnull final String srtpAuth,
                                                       @Nonnull final String srtpCipher) {
            final var rtpCryptoPolicy = new SrtpCryptoPolicyT();

            switch (srtpCipher) {
                case "aes-128-icm":
                    switch (srtpAuth) {
                        case "hmac-sha1-32":
                            libSrtp.srtp_crypto_policy_set_aes_cm_128_hmac_sha1_32(rtpCryptoPolicy);
                            break;
                        case "hmac-sha1-80":
                            libSrtp.srtp_crypto_policy_set_rtp_default(rtpCryptoPolicy);
                            break;
                        default:
                            throw new RuntimeException(String.format(Locale.ROOT,
                                    "Unsupported srtpAuth %s and srtpCipher %s",
                                    srtpAuth, srtpCipher));
                    }
                    break;
                case "aes-256-icm":
                    switch (srtpAuth) {
                        case "hmac-sha1-32":
                            libSrtp.srtp_crypto_policy_set_aes_cm_256_hmac_sha1_32(rtpCryptoPolicy);
                            break;
                        case "hmac-sha1-80":
                            libSrtp.srtp_crypto_policy_set_aes_cm_256_hmac_sha1_80(rtpCryptoPolicy);
                            break;
                        default:
                            throw new RuntimeException(String.format(Locale.ROOT,
                                    "Unsupported srtpAuth %s and srtpCipher %s",
                                    srtpAuth, srtpCipher));
                    }
                    break;
                default:
                    throw new RuntimeException(String.format(Locale.ROOT,
                            "Unsupported srtpCipher %s and srtpAuth %s",
                            srtpCipher, srtpAuth));
            }

            return rtpCryptoPolicy;
        }
    }

    public void close() {
        ((Memory) srtpPolicyT.getKey()).close();
    }
}
