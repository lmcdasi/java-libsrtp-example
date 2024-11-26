package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Structure;
import org.lmcdasi.demo.srtp.common.SrtpSsrcType;

@Structure.FieldOrder({"type", "value"})
@SuppressWarnings({"PMD.BeanMembersShouldSerialize"})
public class SrtpSsrcT extends Structure {
    public int type;
    public int value;
    public SrtpSsrcT() {
        super();
    }

    public SrtpSsrcT(final SrtpSsrcType srtpSsrcType, final int value) {
        super();
        this.type = srtpSsrcType.getValue();
        this.value = value;
    }
}

