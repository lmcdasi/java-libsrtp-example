package org.lmcdasi.demo.srtp.common;

public enum SrtpLogLevelT {
	SRTP_LOG_LEVEL_ERROR(0),
    SRTP_LOG_LEVEL_WARNING(1),
    SRTP_LOG_LEVEL_INFO(2),
    SRTP_LOG_LEVEL_DEBUG(3);

    private final int value;

    SrtpLogLevelT(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
