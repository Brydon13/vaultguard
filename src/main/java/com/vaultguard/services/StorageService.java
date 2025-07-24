package com.vaultguard.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaultguard.model.VaultFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path storageDir;

    public StorageService(String storageDir) {
        this.storageDir = Path.of(storageDir);
    }

    public VaultFile loadVault(String username) throws IOException {
        Path filePath = storageDir.resolve(username + ".json");
        if (!Files.exists(filePath)) {
            return null;
        }
        String json = Files.readString(filePath);
        return gson.fromJson(json, VaultFile.class);
    }

    public void saveVault(String username, VaultFile vaultFile) throws IOException {
        Path filePath = storageDir.resolve(username + ".json");
        Files.createDirectories(storageDir); //incase directory doesn't exist already
        String json = gson.toJson(vaultFile);
        Files.writeString(filePath, json);
    }
}
