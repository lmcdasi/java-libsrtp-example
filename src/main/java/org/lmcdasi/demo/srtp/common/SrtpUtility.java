package org.lmcdasi.demo.srtp.common;

import jakarta.annotation.Nonnull;
import org.apache.commons.codec.binary.Base64;

import java.util.Locale;

public class SrtpUtility {
    public static byte[] getDecodedSrtpKey(@Nonnull final String[] cipherSplit) {
        final var base64EncodedKey = cipherSplit[1].split(":")[1].split("\\|")[0];
        //return Base64.getDecoder().decode(base64EncodedKey.getBytes(StandardCharsets.UTF_8));
        return Base64.decodeBase64(base64EncodedKey);
    }

    public static String getSrtpAuth(@Nonnull final String[] cipherSplit) {
        return cipherSplit[0].substring(11).
                replace("_", "-").toLowerCase(Locale.ROOT);
    }

    public static String getSrtpCipher(@Nonnull final String[] cipherSplit) {
        var srtpCipher = cipherSplit[0].substring(0, 10);
        srtpCipher = srtpCipher.replace("_", "-").toLowerCase(Locale.ROOT);
        if ("AES-CM-128".equalsIgnoreCase(srtpCipher)) {
            srtpCipher = "aes-128-cm";
        }

        // gstreamer conversion from cm to icm
        return srtpCipher.replace("-cm", "-icm");
    }
}
