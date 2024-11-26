package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Pointer;
import org.lmcdasi.demo.srtp.common.SrtpLogLevelT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class LogCallbackImplementation implements LibSrtp.SrtpLogHandlerFuncT {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCallbackImplementation.class);

    @Override
    public void callback(final SrtpLogLevelT level, final String msg, final Pointer data) {
        final var logMsg = String.format(Locale.ROOT, "lib%s", msg);
        switch (level) {
            case SRTP_LOG_LEVEL_ERROR:
                LOGGER.error(logMsg);
                break;
            case SRTP_LOG_LEVEL_WARNING:
                LOGGER.warn(logMsg);
                break;
            case SRTP_LOG_LEVEL_INFO:
                LOGGER.info(logMsg);
                break;
            default:
                LOGGER.debug(logMsg);
                break;
        }
    }
}

