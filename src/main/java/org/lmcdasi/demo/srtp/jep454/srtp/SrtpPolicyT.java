package org.lmcdasi.demo.srtp.jep454.srtp;

import jakarta.annotation.Nonnull;
import lombok.Getter;

import java.lang.foreign.*;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;

public class SrtpPolicyT {
//    private static final AddressLayout C_POINTER = ValueLayout.ADDRESS.withTargetLayout(
//            MemoryLayout.sequenceLayout(java.lang.Long.MAX_VALUE, JAVA_BYTE));

    private static final MemoryLayout SRTP_POLICY_T_LAYOUT = MemoryLayout.structLayout(
            SrtpSsrcT.layout().withName("ssrc"),
            SrtpCryptoPolicyT.layout().withName("rtp"),
            SrtpCryptoPolicyT.layout().withName("rtcp"),
            ValueLayout.ADDRESS.withName("key"),
            ValueLayout.ADDRESS.withName("keys"),
            ValueLayout.JAVA_LONG.withName("num_master_keys"),
            ValueLayout.ADDRESS.withName("deprecated_ekt"),
            ValueLayout.JAVA_LONG.withName("window_size"),
            ValueLayout.JAVA_INT.withName("allow_repeat_tx"),
            MemoryLayout.paddingLayout(4),
            ValueLayout.ADDRESS.withName("enc_xtn_hdr"),
            ValueLayout.JAVA_INT.withName("enc_xtn_hdr_count"),
            MemoryLayout.paddingLayout(4),
            ValueLayout.ADDRESS.withName("next"));

    private static final long SRTP_SSRC_BYTE_OFFSET = SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("ssrc"));
    private static final long SRTP_RTP_BYTE_OFFSET = SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("rtp"));
    private static final long SRTP_RTCP_BYTE_OFFSET = SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("rtcp"));
    private static final long SRTP_KEY_BYTE_OFFSET = SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("key"));
    private static final long SRTP_ALLOW_REPEAT_TX_OFFSET = SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("allow_repeat_tx"));
    private static final long SRTP_NEXT_BYTE_OFFSET = SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("next"));

    @Getter
    private final MemorySegment segment;

    public SrtpPolicyT(@Nonnull final Arena arena) {
        this.segment = arena.allocate(SRTP_POLICY_T_LAYOUT);
    }

    public SrtpSsrcT getSsrc() {
        final var ssrcSegment = segment.asSlice(SRTP_SSRC_BYTE_OFFSET, SrtpSsrcT.layout().byteSize());
        return SrtpSsrcT.of(ssrcSegment);
    }

    public void setSsrc(final MemorySegment srcSegment) {
        final var dstSegment = segment.
                asSlice(SRTP_SSRC_BYTE_OFFSET, SrtpSsrcT.layout().byteSize());
        //MemorySegment.copy(srcSegment, ValueLayout.ADDRESS, 0, dstSegment, ValueLayout.ADDRESS, 0, 1 );
        MemorySegment.copy(srcSegment, 0, dstSegment, 0, SrtpSsrcT.layout().byteSize());
    }

    public SrtpCryptoPolicyT getRtp() {
        final var srtpCryptoPolicySegment = segment.asSlice(SRTP_RTP_BYTE_OFFSET,
                SrtpCryptoPolicyT.layout().byteSize());
        return SrtpCryptoPolicyT.of(srtpCryptoPolicySegment);
    }

    public void setRtp(final MemorySegment srcSegment) {
        final var dstSegment = segment.
                asSlice(SRTP_RTP_BYTE_OFFSET, SrtpCryptoPolicyT.layout().byteSize());
        //MemorySegment.copy(srcSegment, ValueLayout.ADDRESS, 0, dstSegment, ValueLayout.ADDRESS, 0, 1);
        MemorySegment.copy(srcSegment, 0, dstSegment, 0, SrtpCryptoPolicyT.layout().byteSize());
    }

    public SrtpCryptoPolicyT getRtcp() {
        final var srtpCryptoPolicySegment = segment.asSlice(SRTP_RTCP_BYTE_OFFSET,
                SrtpCryptoPolicyT.layout().byteSize());
        return SrtpCryptoPolicyT.of(srtpCryptoPolicySegment);
    }

    public void setRtcp(final MemorySegment srcSegment) {
        final var dstSegment = segment.asSlice(SRTP_RTCP_BYTE_OFFSET,
                SrtpCryptoPolicyT.layout().byteSize());
        //MemorySegment.copy(srcSegment, ValueLayout.ADDRESS, 0, dstSegment, ValueLayout.ADDRESS, 0, 1);
        MemorySegment.copy(srcSegment, 0, dstSegment, 0, SrtpCryptoPolicyT.layout().byteSize());
    }

    public MemorySegment getKey() {
        return segment.get(ValueLayout.ADDRESS, SRTP_KEY_BYTE_OFFSET);
    }

    public void setKey(final MemorySegment keySegment) {
        //final var dstSegment = segment.asSlice(SRTP_KEY_BYTE_OFFSET,
        //        SrtpCryptoPolicyT.layout().byteSize());
        //MemorySegment.copy(srcSegment, ValueLayout.ADDRESS, 0, dstSegment, ValueLayout.ADDRESS, 0, 1);
        segment.set(ValueLayout.ADDRESS, SRTP_KEY_BYTE_OFFSET, keySegment);
    }

    public long getAllowRepeatTx() {
        return segment.get(ValueLayout.JAVA_INT, SRTP_ALLOW_REPEAT_TX_OFFSET);
    }

    public void setAllowRepeatTx(final int allowRepeatTx) {
        segment.set(ValueLayout.JAVA_INT, SRTP_ALLOW_REPEAT_TX_OFFSET, allowRepeatTx);
    }

    /**
    public MemorySegment getEncXtnHdr() {
        return segment.get(ValueLayout.ADDRESS,
                SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("enc_xtn_hdr")));
    }

    public void setEncXtnHdr(final MemorySegment encXtnHdr) {
        segment.set(ValueLayout.ADDRESS,
                SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("enc_xtn_hdr")),
                encXtnHdr);
    }

    public int getEncXtnHdrCount() {
        return segment.get(ValueLayout.JAVA_INT,
                SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("enc_xtn_hdr_count")));
    }

    public void setEncXtnHdrCount(final int encXtnHdrCount) {
        segment.set(ValueLayout.JAVA_INT,
                SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("enc_xtn_hdr_count")),
                encXtnHdrCount);
    }

    public MemorySegment getNext() {
        return segment.get(ValueLayout.ADDRESS,
                SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("enc_xtn_hdr_count")));
    }

    public void setNext(MemorySegment next) {
        segment.set(ValueLayout.ADDRESS,
                SRTP_POLICY_T_LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("enc_xtn_hdr_count")),
                next);
    }
    */
}
