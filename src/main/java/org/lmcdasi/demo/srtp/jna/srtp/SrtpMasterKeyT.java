package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Structure;

@Structure.FieldOrder({"key", "mki_id", "mki_size"})
@SuppressWarnings({"PMD.FieldNamingConventions"})
public class SrtpMasterKeyT extends Structure {
    public String key;
    public String mki_id;
    public int mki_size;

    public static class ByReference extends SrtpMasterKeyT implements Structure.ByReference { }
}

