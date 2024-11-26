package org.lmcdasi.demo.srtp.common;

public enum SrtpSecServ {
    SEC_SERV_NONE(0),
    SEC_SERV_CONF(1),
    SEC_SERV_AUTH(2),
    SEC_SERV_CONF_AND_AUTH(3);

    private final int value;

    SrtpSecServ(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}

