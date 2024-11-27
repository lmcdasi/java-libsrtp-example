package org.lmcdasi.demo.srtp.jni.srtp;

import java.nio.ByteBuffer;

public interface LogCallBackIfc {
    void callback(int level, String msg, ByteBuffer data);
}
