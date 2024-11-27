package org.lmcdasi.demo.srtp.jni.srtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Locale;

public class LogCallbackImplementation implements LogCallBackIfc {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCallbackImplementation.class);

    @Override
    public void callback(int level, String msg, ByteBuffer data) {
        final String logMsg = String.format(Locale.ROOT, "lib%s", msg);
        switch (level) {
            case 0: // SRTP_LOG_LEVEL_ERROR
               LOGGER.error(logMsg);
               break;
            case 1: // SRTP_LOG_LEVEL_WARNING
                LOGGER.warn(logMsg);
                break;
            case 2: // SRTP_LOG_LEVEL_INFO
                LOGGER.info(logMsg);
                break;
            default:
                LOGGER.debug(logMsg);
                break;
        }
    }
}
