package org.lmcdasi.demo.srtp.jep454.srtp;

import jakarta.annotation.Nonnull;
import lombok.Getter;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static java.lang.foreign.MemoryLayout.PathElement.*;

public class SrtpCryptoPolicyT {
    private static final MemoryLayout SRTP_CRYPTO_POLICY_T_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("cipher_type"),
            ValueLayout.JAVA_INT.withName("cipher_key_len"),
            ValueLayout.JAVA_INT.withName("auth_type"),
            ValueLayout.JAVA_INT.withName("auth_key_len"),
            ValueLayout.JAVA_INT.withName("auth_tag_len"),
            ValueLayout.JAVA_INT.withName("sec_serv")
    );

    @Getter
    private final MemorySegment segment;

    public static MemoryLayout layout() {
        return SRTP_CRYPTO_POLICY_T_LAYOUT;
    }

    public static SrtpCryptoPolicyT of(MemorySegment segment) {
        return new SrtpCryptoPolicyT(segment);
    }

    public SrtpCryptoPolicyT(@Nonnull final Arena srtpArena) {
        this.segment = srtpArena.allocate(SRTP_CRYPTO_POLICY_T_LAYOUT);
    }

    private SrtpCryptoPolicyT(MemorySegment segment) {
        this.segment = segment;
    }

    public int getCipherType() {
        return segment.get(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("cipher_type")));
    }

    public void setCipherType(final int value) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("cipher_type")),
                value);
    }

    public int getCipherKeyLen() {
        return segment.get(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("cipher_key_len")));
    }

    public void setCipherKeyLen(final int value) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("cipher_key_len")),
                value);
    }

    public int getAuthType() {
        return segment.get(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("auth_type")));
    }

    public void setAuthType(int value) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("auth_type")),
                value);
    }

    public int getAuthKeyLen() {
        return segment.get(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("auth_key_len")));
    }

    public void setAuthKeyLen(int value) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("auth_key_len")),
                value);
    }

    public int getAuthTagLen() {
        return segment.get(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("auth_tag_len")));
    }

    public void setAuthTagLen(int value) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("auth_tag_len")),
                value);
    }

    public int getSecServ() {
        return segment.get(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("sec_serv")));
    }

    public void setSecServ(int value) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_POLICY_T_LAYOUT.byteOffset(groupElement("sec_serv")),
                value);
    }
}

