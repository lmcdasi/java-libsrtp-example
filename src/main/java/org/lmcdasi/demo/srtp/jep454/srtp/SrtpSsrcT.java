package org.lmcdasi.demo.srtp.jep454.srtp;

import jakarta.annotation.Nonnull;
import org.lmcdasi.demo.srtp.common.SrtpSsrcType;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class SrtpSsrcT {
    private static final MemoryLayout SRTP_CRYPTO_SSRC_T_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("type"),
            ValueLayout.JAVA_INT.withName("value")
    );

    private final MemorySegment segment;

    public static MemoryLayout layout() {
        return SRTP_CRYPTO_SSRC_T_LAYOUT;
    }

    public static SrtpSsrcT of(MemorySegment segment) {
        return new SrtpSsrcT(segment);
    }

    public SrtpSsrcT(@Nonnull final Arena arena) {
        this.segment = arena.allocate(SRTP_CRYPTO_SSRC_T_LAYOUT);
    }

    public SrtpSsrcT(final Arena rtpArena, final SrtpSsrcType srtpSsrcType, final int value) {
        this(rtpArena);
        setType(srtpSsrcType.getValue());
        setValue(value);
    }

    private SrtpSsrcT(MemorySegment segment) {
        this.segment = segment;
    }

    public MemorySegment segment() {
        return segment;
    }

    public void setType(final int type) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_SSRC_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("type")),
                type);
    }

    public void setValue(final int value) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_CRYPTO_SSRC_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("value")),
                value);
    }
}
