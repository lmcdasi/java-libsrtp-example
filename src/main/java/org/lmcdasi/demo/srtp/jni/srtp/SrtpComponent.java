package org.lmcdasi.demo.srtp.jni.srtp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.lmcdasi.demo.srtp.common.SrtpErrStatus;
import org.lmcdasi.demo.srtp.condition.UseJniCondition;
import org.lmcdasi.demo.srtp.jna.srtp.LogCallbackImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;

import static org.lmcdasi.demo.srtp.common.SrtpErrStatus.SRTP_ERR_STATUS_OK;

@Component
@Conditional(UseJniCondition.class)
public class SrtpComponent {
    private final static Logger LOGGER = LoggerFactory.getLogger(SrtpComponent.class);

    private static native int srtpInit();
    private static native int srtpShutdown();
    //private static native int srtp_install_log_handler(LogCallbackImplementation logCallback, Object userData);
    //private static native int srtp_set_debug_module(String moduleName, int level);

    @PostConstruct
    void init() {
        try {
            loadLibraryFromJar();
        } catch (final Throwable t) {
            throw new BeanCreationException("Failed to load native srtp library.", t);
        }

        final var status = srtpInit();
        if (SRTP_ERR_STATUS_OK.getValue() != status) {
            throw new BeanCreationException(STR."srtp_init failed with status: \{status}.");
        }
        LOGGER.info("srtp_init status {}.", Arrays.stream(SrtpErrStatus.values()).
                        filter(e -> e.getValue() == status).findFirst().get());

        /**
        final var logCallbackImpl = new LogCallbackImplementation();

        final var statusLogHandler = srtp_install_log_handler(logCallbackImpl, null);
        if (SRTP_ERR_STATUS_OK.getValue() == statusLogHandler) {
            final var statusDebug = srtp_set_debug_module("srtp", 1);

            LOGGER.info("Activate log handler and srtp debug module");
        }
        */
    }

    @PreDestroy
    void destroy() {
        final var status = srtpShutdown();
        if (SRTP_ERR_STATUS_OK.getValue() != status) {
            LOGGER.info("Status shutting down srtp library {}", status);
        }
    }

    private static void loadLibraryFromJar() throws IOException {
        try (final var inputStream = SrtpComponent.class.getResourceAsStream("/native/libsrtpjni.so")) {
            if (inputStream == null) {
                throw new FileNotFoundException(STR."Library 'native/libsrtpjni.so' not found in JAR.");
            }

            final var tempDir = System.getProperty("java.io.tmpdir");
            final var tempLibFile = new File(tempDir, "libsrtpjni.so");

            try (OutputStream outputStream = new FileOutputStream(tempLibFile)) {
                inputStream.transferTo(outputStream);
            }

            System.load(tempLibFile.getAbsolutePath());
            tempLibFile.deleteOnExit();
        } catch (final Throwable t) {
            throw new BeanCreationException("Unable to load native library", t);
        }
    }
}
