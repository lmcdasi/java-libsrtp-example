package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.platform.EnumConverter;
import org.lmcdasi.demo.srtp.common.SrtpErrStatus;
import org.lmcdasi.demo.srtp.common.SrtpLogLevelT;
import org.lmcdasi.demo.srtp.common.SrtpSecServ;
import org.lmcdasi.demo.srtp.common.SrtpSsrcType;

@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class RecordingJnaTypeMapper extends DefaultTypeMapper {
    RecordingJnaTypeMapper() {
        super();

        addTypeConverter(SrtpErrStatus.class, new EnumConverter<>(SrtpErrStatus.class));
        addTypeConverter(SrtpLogLevelT.class, new EnumConverter<>(SrtpLogLevelT.class));
        addTypeConverter(SrtpSsrcType.class, new EnumConverter<>(SrtpSsrcType.class));
        addTypeConverter(SrtpSecServ.class, new EnumConverter<>(SrtpSecServ.class));
    }
}

