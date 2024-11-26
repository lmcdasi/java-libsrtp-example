package org.lmcdasi.demo.srtp.jep454.srtp;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;
import java.util.Locale;

import org.lmcdasi.demo.srtp.common.SrtpLogLevelT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SrtpLogCallback {
	private static final Logger LOGGER = LoggerFactory.getLogger(SrtpLogCallback.class);

	public static void callback(final MemorySegment level, final MemorySegment msg, final MemorySegment data) {
		final var logMsg = String.format(Locale.ROOT, "lib%s", msg.get(ValueLayout.ADDRESS, 0));
		final var intLevel = level.get(ValueLayout.JAVA_INT, 0);
		final var enumLevel = Arrays.stream(SrtpLogLevelT.values()).filter(e -> e.getValue() == intLevel)
				.findFirst().get();

		switch (enumLevel) {
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
