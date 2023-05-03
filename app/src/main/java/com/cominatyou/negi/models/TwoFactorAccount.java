package com.cominatyou.negi.models;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.Serializable;

public class TwoFactorAccount implements Serializable {
    private final String name;
    private final String username;
    private final String secret;
    private final String id;

    public TwoFactorAccount(String name, String username, String secret, String id) {
        this.name = name;
        this.username = username;
        this.secret = secret;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getSecret() {
        return secret;
    }

    public String getId() {
        return id;
    }

    public String getHexEncodedSecret() {
        final Base32 base32 = new Base32();
        final byte[] bytes = base32.decode(secret);
        return Hex.encodeHexString(bytes);
    }
}
