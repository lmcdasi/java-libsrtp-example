package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
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

    private final IntByReference rtpPacketLength;
    private final LibSrtp libSrtp;
    private final Lock sessionLock;
    private final PointerByReference session;
    private final SrtpContext srtpContext;

    public SrtpSession(@Nonnull final LibSrtp libSrtp, @Nonnull final String cipher) {
        this.rtpPacketLength = new IntByReference();
        this.libSrtp = libSrtp;
        this.srtpContext = createSrtpContext(cipher);
        this.session = createSrtpSession(this.srtpContext);
        this.sessionLock = new ReentrantLock();
    }

    @Override
    public void endSession() {
        if (session != null && session.getValue() != null) {
            final var sessionValue = session.getValue();
            sessionLock.lock();
            try {
                final var srtpErrorStatus = libSrtp.srtp_dealloc(sessionValue);
                // TODO: error handling
                session.setValue(null);
            } finally {
                sessionLock.unlock();
            }

            srtpContext.close();
        }
    }

    @Override
    public @Nonnull ByteBuffer unprotectPacket(@Nonnull RtpPacket rtpPacket) {
        final var rtpPacketData = Native.getDirectBufferPointer(rtpPacket.getNativeByteBuffer());
        rtpPacketLength.setValue(rtpPacket.getRtpLength());

        sessionLock.lock();
        try {
            final var sessionPointer = session.getValue();
            if (sessionPointer != null) {
                final var srtpErrStatus = libSrtp.srtp_unprotect(sessionPointer, rtpPacketData,
                            rtpPacketLength);
                if (SRTP_ERR_STATUS_OK.equals(srtpErrStatus)) {
                    final var decryptedRtpPacketLength = rtpPacketLength.getValue() - RTP_HEADER_SIZE;
                    return rtpPacketData.getByteBuffer(RTP_HEADER_SIZE, decryptedRtpPacketLength);
                }
                LOGGER.error("Failed to decrypt rtp packet. Error {}", srtpErrStatus);
            }
        } catch (final Throwable t) {
            LOGGER.error("Unexpected error attempt decrypt packet", t);
        } finally {
            sessionLock.unlock();
        }

        return EMPTY_BYTE_BUFFER;
    }

    private @Nonnull SrtpContext createSrtpContext(@Nonnull final String cipher) {
        final var srtpContextBuilder = new SrtpContext.Builder().withLibSrtp(libSrtp).withCipher(cipher);
        return srtpContextBuilder.build();
    }

    private @Nonnull PointerByReference createSrtpSession(@Nonnull final SrtpContext srtpContext) {
        PointerByReference session = new PointerByReference();
        final var srtpErrStatus = libSrtp.srtp_create(session, srtpContext.getSrtpPolicyT());
        // TODO: error handling
        return session;
    }
}
