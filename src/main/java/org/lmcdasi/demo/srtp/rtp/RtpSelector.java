package org.lmcdasi.demo.srtp.rtp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.lmcdasi.demo.srtp.ApplicationProperties;
import org.lmcdasi.demo.srtp.util.CustomThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Component
public class RtpSelector {
    private static final Logger LOGGER = LoggerFactory.getLogger(RtpSelector.class);

    private ApplicationProperties applicationProperties;
    private ExecutorService executor;
    private Future<?> future;
    @Getter
    private Selector rtpSelector;

    @Autowired
    public void setApplicationProperties(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void init() throws IOException {
        final var customThreadFactory = new CustomThreadFactoryBuilder().setDaemon(true)
                .setNamePrefix("rtp-selector-d%").build();
        executor = Executors.newSingleThreadExecutor(customThreadFactory);
        rtpSelector = Selector.open();

        future = executor.submit(() -> selectorRunnable(rtpSelector));
    }

    @PreDestroy
    public void destroy() {
        try {
            future.cancel(true);
            executor.shutdownNow();
            rtpSelector.close();
        } catch (final Exception ex) {
            // ignore
        }
    }

    private void selectorRunnable(final Selector selector) {
        try (selector) {
            final var rtpSelectorTimeout = applicationProperties.getRtpSelectorTimeoutMs();
            final Consumer<SelectionKey> processChannelDataConsumer = key -> {
                if (key.isValid() && key.isReadable()) {
                    ((RtpChannel) key.attachment()).processChannelData(key);
                }
            };

            while (selector.isOpen()) {
                selector.select(rtpSelectorTimeout.toMillis());

                final var selectedKeys = selector.selectedKeys();
                selectedKeys.forEach(processChannelDataConsumer);
                selectedKeys.clear();
            }
        } catch (final Exception e) {
            LOGGER.error("Selector error.", e);
        }

        // TODO: trigger shutdown
        //final var connectorShutdownApplicationEvent = new ConnectorShutdownApplicationEvent(this, "Selector failure.");
        //applicationEventPublisher.publishEvent(connectorShutdownApplicationEvent);
    }
}
