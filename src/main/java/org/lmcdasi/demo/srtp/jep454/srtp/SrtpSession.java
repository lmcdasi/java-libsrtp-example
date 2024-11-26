package org.lmcdasi.demo.srtp.jep454.srtp;

import jakarta.annotation.Nonnull;
import org.lmcdasi.demo.srtp.common.SrtpSessionIfc;
import org.lmcdasi.demo.srtp.rtp.RtpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.lmcdasi.demo.srtp.common.SrtpErrStatus.SRTP_ERR_STATUS_OK;
import static org.lmcdasi.demo.srtp.rtp.RtpConstants.RTP_HEADER_SIZE;

public class SrtpSession implements SrtpSessionIfc {
    private static final Logger LOGGER = LoggerFactory.getLogger(SrtpSession.class);

    private final Arena srtpArena;
    private final MemorySegment rtpPacketLength;
    private final SrtpComponent.LibSrtpMehodMap libSrtpMehodMap;
    private final Lock sessionLock;
    private final MemorySegment session;

    public SrtpSession(@Nonnull final SrtpComponent.LibSrtpMehodMap libSrtpMehodMap, @Nonnull final String cipher) {
        this.srtpArena = Arena.ofShared();
        this.rtpPacketLength = srtpArena.allocate(ValueLayout.JAVA_INT);
        this.libSrtpMehodMap = libSrtpMehodMap;
        final var srtpContext = createSrtpContext(cipher);
        this.session = createSrtpSession(srtpContext);
        this.sessionLock = new ReentrantLock();
    }

    @Override
    public void endSession() {
        if (session != null) {
            final var sessionPointer = session.get(ValueLayout.ADDRESS, 0);
            if (sessionPointer != MemorySegment.NULL) {
                sessionLock.lock();
                try {
                    final var srtpErrorStatus = libSrtpMehodMap.getSrtp_dealloc().invoke(sessionPointer);
                    // TODO: error handling
                } catch (final Throwable e) {
                } finally {
                    sessionPointer.set(ValueLayout.ADDRESS, 0, MemorySegment.NULL);
                    sessionLock.unlock();
                }
            }
        }

        srtpArena.close();
    }

    @Override
    public @Nonnull ByteBuffer unprotectPacket(@Nonnull RtpPacket rtpPacket) {
        final var rtpPacketAddress = MemorySegment.ofBuffer(rtpPacket.getNativeByteBuffer());
        rtpPacketLength.set(ValueLayout.JAVA_INT, 0, rtpPacket.getRtpLength());

        sessionLock.lock();
        try {
            final var sessionPointer = session.get(ValueLayout.ADDRESS, 0);
            if (sessionPointer != MemorySegment.NULL) {
                final var srtpErrStatus = (int) libSrtpMehodMap.getSrtp_unprotect().
                        invoke(sessionPointer, rtpPacketAddress, rtpPacketLength);
                if (SRTP_ERR_STATUS_OK.getValue() == srtpErrStatus) {
                    final var decryptedRtpPacketLength = rtpPacketLength.get(ValueLayout.JAVA_INT, 0) - RTP_HEADER_SIZE;
                    return rtpPacketAddress.asSlice(RTP_HEADER_SIZE, decryptedRtpPacketLength).asByteBuffer();
                }
                LOGGER.error("Failed to decrypt packet with srtp status {}", srtpErrStatus);
            }
        } catch (final Throwable t) {
            LOGGER.error("Unexpected error attempt decrypt packet", t);
        } finally {
            sessionLock.unlock();
        }

        return EMPTY_BYTE_BUFFER;
    }

    private @Nonnull SrtpContext createSrtpContext(@Nonnull final String cipher) {
        final var srtpContextBuilder = new SrtpContext.Builder().withCipher(cipher).
                withLibSrtpMehodMap(libSrtpMehodMap).withStrpArena(srtpArena);
        return srtpContextBuilder.build();
    }

    private @Nonnull MemorySegment createSrtpSession(@Nonnull final SrtpContext srtpContext) {
        final var session = srtpArena.allocate(ValueLayout.ADDRESS);
        try {
            final var srtpErrStatus = libSrtpMehodMap.getSrtp_create().invoke(session,
                    srtpContext.getSrtpPolicyT().getSegment());
            // TODO: error handling
        } catch (final Throwable throwable) {
            throw new BeanCreationException(throwable.getLocalizedMessage());
        }
        return session;
    }
}
