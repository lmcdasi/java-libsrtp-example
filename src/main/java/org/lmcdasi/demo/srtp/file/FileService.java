package org.lmcdasi.demo.srtp.file;

import org.lmcdasi.demo.srtp.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileService {
    private ApplicationProperties applicationProperties;

    @Autowired
    public void setApplicationProperties(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public FileChannel createFile(final String udpPort) throws IOException {
        final var filePath = STR."\{applicationProperties.getFilePath()}decoded-\{udpPort}.raw";
        return FileChannel.open(Paths.get(filePath), StandardOpenOption.APPEND, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
    }

    public void writeToFile(final FileChannel fileChannel, final ByteBuffer decryptedRtpBuffer) throws IOException {
        fileChannel.write(decryptedRtpBuffer);
    }
}
