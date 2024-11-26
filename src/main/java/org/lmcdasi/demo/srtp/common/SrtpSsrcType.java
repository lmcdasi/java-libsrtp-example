package org.lmcdasi.demo.srtp.common;

public enum SrtpSsrcType {
    SSRC_UNDEFINED(0),
    SSRC_SPECIFIC(1),
    SSRC_ANY_INBOUND(2),
    SSRC_ANY_OUTBOUND(3);

    private final int value;

    SrtpSsrcType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}

