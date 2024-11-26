package org.lmcdasi.demo.srtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "application")
@Getter @Setter @ToString
public class ApplicationProperties {
	private static Logger LOGGER = LoggerFactory.getLogger(ApplicationProperties.class);

	private boolean debugSrtp;
	private String filePath;
	private String ipAddress;
	private Duration rtpSelectorTimeoutMs;
	private boolean useBuiltinNative;
	private boolean useJna;
	private boolean useJni;

	@PostConstruct
	public void postConstruct() {
		LOGGER.info("Current ApplicationProperties loaded are {}", this);
	}
}
