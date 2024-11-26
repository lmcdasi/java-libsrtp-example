package org.lmcdasi.demo.srtp.rtp;

import jakarta.annotation.Nonnull;
import org.lmcdasi.demo.srtp.util.HostAddressService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class RtpChannel {
    private static final int SRTP_PACKET_PCM_SIZE = 182; // 182=12 rtp header + 160 payload + 10 hmac

    private final LinkedBlockingQueue<RtpPacket> linkedBlockingQueue = new LinkedBlockingQueue<>();
    private final DatagramChannel rtpDatagramChannel;

    public RtpChannel(@Nonnull final HostAddressService hostAddressService,
                      @Nonnull final Selector rtpSelector,
                      final int udpPort) {
        try {
            final var socketAddress = new InetSocketAddress(hostAddressService.getHostAddress(), udpPort);
            rtpDatagramChannel = DatagramChannel.open();
            rtpDatagramChannel.configureBlocking(false);
            rtpDatagramChannel.socket().bind(socketAddress);
            rtpDatagramChannel.register(rtpSelector, SelectionKey.OP_READ, this);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (Objects.nonNull(rtpDatagramChannel)) {
            try {
                rtpDatagramChannel.close();
            } catch (final IOException ioException) {
                // ignore
            }
        }
    }

    public RtpPacket take() throws InterruptedException {
        return linkedBlockingQueue.take();
    }

    public void processChannelData(final SelectionKey key) {
        try {
            final var directByteBuffer = ByteBuffer.allocateDirect(SRTP_PACKET_PCM_SIZE);
            ((DatagramChannel) key.channel()).receive(directByteBuffer);
            directByteBuffer.flip();

            linkedBlockingQueue.offer(new RtpPacket(directByteBuffer));
        } catch (final Exception e) {
            final var isInterrupted = Thread.interrupted();
            if (isInterrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
