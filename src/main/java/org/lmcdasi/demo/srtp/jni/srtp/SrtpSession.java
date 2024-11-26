package org.lmcdasi.demo.srtp.jni.srtp;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import jakarta.annotation.Nonnull;
import org.lmcdasi.demo.srtp.common.SrtpSessionIfc;
import org.lmcdasi.demo.srtp.rtp.RtpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.lmcdasi.demo.srtp.common.SrtpErrStatus.SRTP_ERR_STATUS_OK;
import static org.lmcdasi.demo.srtp.rtp.RtpConstants.RTP_HEADER_SIZE;

public class SrtpSession implements SrtpSessionIfc {
    private static final Logger LOGGER = LoggerFactory.getLogger(SrtpSession.class);

    private static native long createSrtpContext(String cipher);
    private static native int destroySrtpContext(long srtpContext);
    private static native long createSrtpSession(long srtpContext);
    private static native int destroySrtpSession(long session);
    private static native int srtpUnprotect(long session, ByteBuffer rtpPacketData, int[] rtpPacketLength);

    private final Lock sessionLock;
    private final long srtpContext;
    private final long session;

    public SrtpSession(@Nonnull final String cipher) {
        this.srtpContext = createSrtpContext(cipher);
        this.session = createSrtpSession(srtpContext);
        this.sessionLock = new ReentrantLock();
    }

    @Override
    public void endSession() {
            sessionLock.lock();
            try {
                final var srtpErrorStatus = destroySrtpSession(session);
            } finally {
                sessionLock.unlock();
            }
            destroySrtpContext(srtpContext);
    }

    @Override
    public @Nonnull ByteBuffer unprotectPacket(@Nonnull RtpPacket rtpPacket) {
        final var rtpPacketData = rtpPacket.getNativeByteBuffer();
        int[] rtpPacketLength = {rtpPacket.getRtpLength()};

        sessionLock.lock();
        try {
                final var srtpErrStatus = srtpUnprotect(session, rtpPacketData, rtpPacketLength);
                if (SRTP_ERR_STATUS_OK.getValue() == srtpErrStatus) {
                    final var decryptedRtpPacketLength = rtpPacketLength[0] - RTP_HEADER_SIZE;
                    return rtpPacketData.slice(RTP_HEADER_SIZE, decryptedRtpPacketLength);
                }
                LOGGER.error("Failed to decrypt rtp packet. Error {}", srtpErrStatus);
        } catch (final Throwable t) {
            LOGGER.error("Unexpected error attempt decrypt packet", t);
        } finally {
            sessionLock.unlock();
        }

        return EMPTY_BYTE_BUFFER;
    }
}
