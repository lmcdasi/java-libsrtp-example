package org.lmcdasi.demo.srtp.rtp;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Locale;

import static org.lmcdasi.demo.srtp.rtp.RtpConstants.RTP_HEADER_SIZE;

public class RtpPacket {
    private int dataLength;
    @Getter
    private final ByteBuffer nativeByteBuffer;
    @Getter
    private int rtpLength;
    private final long sequenceNumber;

    public RtpPacket(final ByteBuffer directByteBuffer) {
        final var  payloadType = directByteBuffer.get(1) & 0x7f;
        if (payloadType > 71 && payloadType < 96) {
            throw new RtpUnsupportedPacketException(String.format(Locale.ROOT, "Invalid rtp payload type [%d]",
                    payloadType));
        }

        this.nativeByteBuffer = directByteBuffer;
        this.sequenceNumber = computeSequenceNumber();
        updateRtpPacketLength(directByteBuffer.limit());
    }

    public long getSsrc() {
        if (sequenceNumber > -1) {
            return (((long) (nativeByteBuffer.get(8) & 0xff) << 24) |
                    ((long) (nativeByteBuffer.get(9) & 0xff) << 16) |
                    ((long) (nativeByteBuffer.get(10) & 0xff) << 8) |
                    ((long) (nativeByteBuffer.get(11) & 0xff))) & 0xffffffffL;
        }
        return -1;
    }

    public long getTimeStamp() {
        if (sequenceNumber > -1) {
            return ((long) (nativeByteBuffer.get(4) & 0xff) << 24) |
                    ((long) (nativeByteBuffer.get(5) & 0xff) << 16) |
                    ((long) (nativeByteBuffer.get(6) & 0xff) << 8) |
                    ((long) (nativeByteBuffer.get(7) & 0xff));
        }
        return -1;
    }

    private int computeSequenceNumber() {
        return (nativeByteBuffer.get(2) & 0xff) << 8 |
                (nativeByteBuffer.get(3) & 0xff);
    }

    private void updateRtpPacketLength(final int rtpLength) {
        this.rtpLength = rtpLength;
        this.dataLength = this.rtpLength - RTP_HEADER_SIZE;
    }
}
