package org.lmcdasi.demo.srtp.rest;

import org.apache.logging.log4j.util.Strings;
import org.lmcdasi.demo.srtp.rtp.RtpConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RtpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RtpController.class);

    private final Map<String, Thread> rtpConsumerTaskMap = new HashMap<>();

    private RtpConsumerService rtpConsumerService;

    @Autowired
    public void setRtpConsumerService(final RtpConsumerService rtpConsumerService) {
        this.rtpConsumerService = rtpConsumerService;
    }

    @GetMapping("/StartRecording") public String startRecording(@RequestParam String udpPort, @RequestParam String cipher) {
        LOGGER.info("Got StartRecording with udpPort {} and cipher {}", udpPort, cipher);

        if (Strings.isNotEmpty(udpPort) && Strings.isNotEmpty(cipher)) {
            rtpConsumerTaskMap.put(udpPort, rtpConsumerService.createRtpConsumerTask(udpPort, cipher));
        }

        return "Started";
    }

    @GetMapping("/StopRecording") public String stopRecording(@RequestParam String udpPort) {
        rtpConsumerTaskMap.get(udpPort).interrupt();

        return "Stopped";
    }
}
