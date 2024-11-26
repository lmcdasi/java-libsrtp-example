package org.lmcdasi.demo.srtp.jep454.srtp;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.lmcdasi.demo.srtp.common.SrtpErrStatus;
import org.lmcdasi.demo.srtp.common.SrtpSecServ;
import org.lmcdasi.demo.srtp.common.SrtpSsrcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.*;
import java.util.Locale;

import static org.lmcdasi.demo.srtp.common.SrtpUtility.*;

final class SrtpContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(SrtpContext.class);

    private final Arena arenaConfined;
    @Getter
    private final SrtpPolicyT srtpPolicyT;

    private SrtpContext(@Nonnull final Arena arenaConfined, @Nonnull final SrtpPolicyT srtpPolicyT) {
        this.arenaConfined = arenaConfined;
        this.srtpPolicyT = srtpPolicyT;
    }

    public static final class Builder {
        private Arena arenaConfined;
        private String cipher;
        private SrtpComponent.LibSrtpMehodMap libSrtpMehodMap;

        public @Nonnull Builder withCipher(@Nonnull final String cipher) {
            this.cipher = cipher;
            return this;
        }

        public @Nonnull Builder withLibSrtpMehodMap(@Nonnull final SrtpComponent.LibSrtpMehodMap libSrtpMehodMap) {
            this.libSrtpMehodMap = libSrtpMehodMap;
            return this;
        }

        public @Nonnull Builder withStrpArena(@Nonnull final Arena srtpArena) {
            this.arenaConfined = srtpArena;
            return this;
        }

        SrtpContext build() {
            final var cipherSplit = cipher.split(" ");
            final var srtpAuth = getSrtpAuth(cipherSplit);
            final var srtpCipher = getSrtpCipher(cipherSplit);

            final var rtpCryptoPolicy = getSrtpCryptoPolicyT(arenaConfined, srtpAuth, srtpCipher);
            rtpCryptoPolicy.setSecServ(SrtpSecServ.SEC_SERV_CONF_AND_AUTH.getValue());

            final var rtcpCryptoPolicy = new SrtpCryptoPolicyT(arenaConfined);
            rtcpCryptoPolicy.setSecServ(SrtpSecServ.SEC_SERV_NONE.getValue());

            final var srtpPolicy = new SrtpPolicyT(arenaConfined);
            srtpPolicy.setAllowRepeatTx(0);

            final var srtpKey = getDecodedSrtpKey(cipherSplit);
            final var srtpKeyNative = arenaConfined.allocateArray(ValueLayout.JAVA_BYTE, srtpKey);
            srtpPolicy.setKey(srtpKeyNative);

            srtpPolicy.setRtp(rtpCryptoPolicy.getSegment());
            srtpPolicy.setRtcp(rtcpCryptoPolicy.getSegment());
            final var srtpSsrc = new SrtpSsrcT(arenaConfined, SrtpSsrcType.SSRC_ANY_INBOUND, 0);
            srtpPolicy.setSsrc(srtpSsrc.segment());
            //srtpPolicy.setWindow_size(applicationPropertiesSrtp.getWindowSize());

            try {
                final var session = arenaConfined.allocate(ValueLayout.ADDRESS);
                final var srtpErrStatus = (int) libSrtpMehodMap.getSrtp_create().invoke(session, srtpPolicy.getSegment());
                if (SrtpErrStatus.SRTP_ERR_STATUS_OK.getValue() != srtpErrStatus) {
                    LOGGER.error("srtp_create failed with error {}", srtpErrStatus);
                }
            } catch (final Throwable t) {
                // TODO: error handling
            }

            return new SrtpContext(arenaConfined, srtpPolicy);
        }

        @SuppressWarnings("PMD.CyclomaticComplexity")
        private SrtpCryptoPolicyT getSrtpCryptoPolicyT(@Nonnull final Arena srtpArena,
                                                       @Nonnull final String srtpAuth,
                                                       @Nonnull final String srtpCipher) {
            final var rtpCryptoPolicy = new SrtpCryptoPolicyT(srtpArena);

            switch (srtpCipher) {
                case "aes-128-icm":
                    switch (srtpAuth) {
                        case "hmac-sha1-32":
                            try {
                                libSrtpMehodMap.getSrtp_crypto_policy_set_aes_cm_128_hmac_sha1_32().invoke(rtpCryptoPolicy.getSegment());
                            } catch (final Throwable t) {
                                throw new RuntimeException("Failed to invoke srtp_crypto_policy_set_aes_cm_128_hmac_sha1_32");
                            }
                            break;
                        case "hmac-sha1-80":
                            try {
                                libSrtpMehodMap.getSrtp_crypto_policy_set_rtp_default().invoke(rtpCryptoPolicy.getSegment());
                            } catch (final Throwable t) {
                                throw new RuntimeException("Failed to invoke srtp_crypto_policy_set_rtp_default", t);
                            }
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
                            try {
                                libSrtpMehodMap.getSrtp_crypto_policy_set_aes_cm_256_hmac_sha1_32().invoke(rtpCryptoPolicy.getSegment());
                            } catch (final Throwable t) {
                                throw new RuntimeException("Failed to invoke srtp_crypto_policy_set_aes_cm_256_hmac_sha1_32");
                            }
                            break;
                        case "hmac-sha1-80":
                            try {
                                libSrtpMehodMap.getSrtp_crypto_policy_set_aes_cm_256_hmac_sha1_80().invoke(rtpCryptoPolicy.getSegment());
                            } catch (final Throwable t) {
                                throw new RuntimeException("Failed to invoke srtp_crypto_policy_set_aes_cm_256_hmac_sha1_80");
                            }
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
}
