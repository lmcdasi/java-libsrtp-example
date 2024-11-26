package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import lombok.Getter;
import lombok.Setter;

@Setter
@Structure.FieldOrder({"ssrc", "rtp", "rtcp", "key", "keys", "num_master_keys", "deprecated_ekt",
        "window_size", "allow_repeat_tx", "enc_xtn_hdr", "enc_xtn_hdr_count", "next"
})
@SuppressWarnings("PMD.FieldNamingConventions")
public class SrtpPolicyT extends Structure {
    public SrtpSsrcT ssrc;
    @Getter
    public SrtpCryptoPolicyT rtp;
    public SrtpCryptoPolicyT rtcp;
    @Getter
    public Pointer key;
    public SrtpMasterKeyT.ByReference keys;
    public long num_master_keys;
    public Pointer deprecated_ekt;
    public long window_size;
    public int allow_repeat_tx;
    public IntByReference enc_xtn_hdr;
    public int enc_xtn_hdr_count;
    public SrtpPolicyT.ByReference  next;

    public static class ByReference extends SrtpPolicyT implements Structure.ByReference { }
}

