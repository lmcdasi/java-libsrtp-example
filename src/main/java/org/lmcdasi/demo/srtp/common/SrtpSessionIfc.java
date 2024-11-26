package org.lmcdasi.demo.srtp.common;

import jakarta.annotation.Nonnull;
import org.lmcdasi.demo.srtp.rtp.RtpPacket;

import java.nio.ByteBuffer;

public interface SrtpSessionIfc {
    ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);

    void endSession();
    ByteBuffer unprotectPacket(@Nonnull final RtpPacket rtpPacket);
}
