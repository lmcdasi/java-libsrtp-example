package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Library;
import com.sun.jna.Native;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.lmcdasi.demo.srtp.ApplicationProperties;
import org.lmcdasi.demo.srtp.condition.UseJnaCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.lmcdasi.demo.srtp.common.SrtpErrStatus.SRTP_ERR_STATUS_OK;

@Component
@Conditional(UseJnaCondition.class)
public class SrtpComponent {
    private final static Logger LOGGER = LoggerFactory.getLogger(SrtpComponent.class);

    private ApplicationProperties applicationProperties;
    private LogCallbackImplementation logCallbackImpl;

    @Getter
    private LibSrtp libSrtp;

    @Autowired
    public void setApplicationProperties(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    void init() {
        final var options = new HashMap<String, Object>();
        options.put(Library.OPTION_TYPE_MAPPER, new RecordingJnaTypeMapper());

        libSrtp = Native.load("srtp2", LibSrtp.class, options);
        final var srtpErrStatusT = libSrtp.srtp_init();
        LOGGER.info("Status instantiating srtp library {}", srtpErrStatusT);
        if (!SRTP_ERR_STATUS_OK.equals(srtpErrStatusT)) {
            throw new BeanCreationException("Failed to load native srtp library");
        }

        if (applicationProperties.isDebugSrtp()) {
            logCallbackImpl = new LogCallbackImplementation();
            LOGGER.info("Status enabling srtp library log handler {}", libSrtp.srtp_install_log_handler(logCallbackImpl, null));
            LOGGER.info("Status enabling debug srtp library {}", libSrtp.srtp_set_debug_module("srtp", 1));
        }

        LOGGER.info("JNA SrtpComponent loaded ...");
    }

    @PreDestroy
    void shutdown() {
        final var srtpErrStatusT = libSrtp.srtp_shutdown();
        LOGGER.info("Status shutting down srtp library {}", srtpErrStatusT);
    }
}
