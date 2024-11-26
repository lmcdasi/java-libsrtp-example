package org.lmcdasi.demo.srtp.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.lmcdasi.demo.srtp.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class HostAddressService {
    private ApplicationProperties applicationProperties;

    @Getter
    private String hostAddress;

    @PostConstruct
    public void init() throws UnknownHostException {
        final var bindToIpAddress = applicationProperties.getIpAddress();
        hostAddress = InetAddress.getByName(bindToIpAddress).getHostAddress();
    }

    @Autowired
    public void setApplicationProperties(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}
