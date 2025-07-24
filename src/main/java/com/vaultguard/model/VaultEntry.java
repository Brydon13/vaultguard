package com.vaultguard.model;

public class VaultEntry {
    public String name;
    public EncryptedData encryptedKey;

    public VaultEntry(String name, EncryptedData encryptedKey) {
        this.name = name;
        this.encryptedKey = encryptedKey;
    }
}
