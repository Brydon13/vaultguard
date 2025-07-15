package com.vaultguard.model;

import java.util.ArrayList;
import java.util.List;

public class VaultFile {
    public String salt;
    public List<VaultEntry> keys = new ArrayList<>();

    public VaultFile(String salt, List<VaultEntry> keys) {
        this.salt = salt;
        this.keys = keys;
    }
}
