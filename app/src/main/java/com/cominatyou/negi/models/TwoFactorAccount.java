package com.cominatyou.negi.models;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.Serializable;

public class TwoFactorAccount implements Serializable {
    private final String name;
    private final String username;
    private final String secret;

    public TwoFactorAccount(String name, String username, String secret) {
        this.name = name;
        this.username = username;
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getSecret() {
        final Base32 base32 = new Base32();
        final byte[] bytes = base32.decode(secret);
        return Hex.encodeHexString(bytes);
    }
}
