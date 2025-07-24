package com.vaultguard.model;

public class EncryptedData {
    private String iv;
    private String ciphertext;

    public EncryptedData(String iv, String ciphertext) {
        this.iv = iv;
        this.ciphertext = ciphertext;
    }

    public String getIv() {
        return iv;
    }

    public String getCiphertext() {
        return ciphertext;
    }
}