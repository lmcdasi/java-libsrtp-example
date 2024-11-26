package org.lmcdasi.demo.srtp.rtp;

import jakarta.annotation.Nonnull;
import org.lmcdasi.demo.srtp.util.HostAddressService;
import org.lmcdasi.demo.srtp.common.SrtpSessionFactory;
import org.lmcdasi.demo.srtp.common.SrtpSessionIfc;
import org.lmcdasi.demo.srtp.file.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Service
public class RtpConsumerService {
    private FileService fileService;
    private HostAddressService hostAddressService;
    private RtpSelector rtpSelector;
    private SrtpSessionFactory srtpSessionFactory;

    @Autowired
    public void setFileService(final FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setHostAddressService(final HostAddressService hostAddressService) {
        this.hostAddressService = hostAddressService;
    }

    @Autowired
    public void setSrtpSessionFactory(final SrtpSessionFactory srtpSessionFactory) {
        this.srtpSessionFactory = srtpSessionFactory;
    }

    @Autowired
    public void setRtpSelector(final RtpSelector rtpSelector) {
        this.rtpSelector = rtpSelector;
    }

    public class RtpConsumerTask implements Runnable {
        private static final Logger LOGGER = LoggerFactory.getLogger(RtpConsumerTask.class);

        private final String udpPort;
        private final RtpChannel rtpChannel;
        private final SrtpSessionIfc srtpSession;

        RtpConsumerTask(final HostAddressService hostAddressService, final RtpSelector rtpSelector, String udpPort,
                        String cipher) {
            this.udpPort = udpPort;
            rtpChannel = new RtpChannel(hostAddressService, rtpSelector.getRtpSelector(), Integer.parseInt(udpPort));

            srtpSession = srtpSessionFactory.buildSrtpSession(cipher);
        }

        @Override
        public void run() {
            LOGGER.info("RtpConsumerTask started");

            try(final var fileChannel = fileService.createFile(udpPort)) {
                do {
                    final var rtpPacket = rtpChannel.take();
                    final var start = Instant.now();
                    final var decryptedRtpPacket = srtpSession.unprotectPacket(rtpPacket);
                    LOGGER.info("Decryption took {}", Duration.between(start, Instant.now()));
                    fileService.writeToFile(fileChannel, decryptedRtpPacket);
                } while (true);
            } catch (final InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            } catch (final IOException e) {
                LOGGER.error("Unexpected error", e);
            } finally {
                srtpSession.endSession();
                rtpChannel.close();
            }

            LOGGER.info("RtpConsumerTask ended");
        }
    }

    public Thread createRtpConsumerTask(@Nonnull String udpPort, @Nonnull String srtpCipher) {
        final var rtpConsumerTask = new RtpConsumerTask(hostAddressService, rtpSelector, udpPort, srtpCipher);

        final var thread = new Thread(rtpConsumerTask);
        thread.start();

        return thread;
    }
}
