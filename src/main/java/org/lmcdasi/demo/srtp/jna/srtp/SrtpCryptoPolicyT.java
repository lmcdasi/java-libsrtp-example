package org.lmcdasi.demo.srtp.jna.srtp;

import com.sun.jna.Structure;
import lombok.Getter;
import lombok.Setter;

@Setter
@Structure.FieldOrder({"cipher_type", "cipher_key_len", "auth_type", "auth_key_len", "auth_tag_len", "sec_serv"})
@SuppressWarnings({"PMD.FieldNamingConventions"})
public class SrtpCryptoPolicyT extends Structure {
    public int cipher_type;
    public int cipher_key_len;
    public int auth_type;
    public int auth_key_len;
    @Getter
    public int auth_tag_len;
    public int sec_serv;
}

