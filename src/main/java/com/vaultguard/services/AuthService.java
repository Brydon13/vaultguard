package com.vaultguard.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthService {

    private UserService userService = new UserService();
    private EncryptionService encryptionService = new EncryptionService();

    // ***************** Login Business Logic Module BEGIN *****************

    /**
     * Attempts to authenticate the user by checking their credentials
     * against their vault file.
     *
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        // 1. Validate input format first
        if (!userService.validateLogin(username, password)) return false;

        // 2. Check for vault file
        Path vaultPath = Paths.get("vaults", username + ".json");
        if (!Files.exists(vaultPath)) return false;

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> vaultData = mapper.readValue(Files.readAllBytes(vaultPath), Map.class);

            String saltBase64 = (String) vaultData.get("salt");
            byte[] salt = Base64.getDecoder().decode(saltBase64);

            Map<String, Object> keys = (Map<String, Object>) vaultData.get("keys");
            Map<String, String> authEntry = (Map<String, String>) keys.get("vaultguard-auth");

            EncryptionService.EncryptedData encryptedAuth = new EncryptionService.EncryptedData(
                authEntry.get("iv"),
                authEntry.get("ciphertext")
            );

            // Attempt to decrypt auth entry
            encryptionService.decrypt(encryptedAuth, password, salt);
            return true; // Success

        } catch (Exception e) {
            // Any failure means login failed (bad password, file, etc)
            return false;
        }
    }
    public boolean registerNewUser(String username, String password) {
    // Validate input format
    if (!userService.validateRegistration(username, password)) return false;

    // Check if the user already exists
    Path vaultPath = Paths.get("vaults", username + ".json");
    if (Files.exists(vaultPath)) return false; // Username taken

    try {
        // Generate salt and encrypt the auth key
        byte[] salt = userService.generateSalt();
        EncryptionService.EncryptedData ed = encryptionService.encrypt("dummy", password, salt);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        Map<String, String> authKey = new HashMap<>();
        authKey.put("iv", ed.getIv());
        authKey.put("ciphertext", ed.getCiphertext());

        Map<String, Object> keys = new HashMap<>();
        keys.put("vaultguard-auth", authKey);

        Map<String, Object> vaultData = new HashMap<>();
        vaultData.put("salt", saltBase64);
        vaultData.put("keys", keys);

        // Write to vaults/username.json
        Files.createDirectories(Paths.get("vaults"));
        ObjectMapper mapper = new ObjectMapper();
        Files.write(
          vaultPath,
          mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(vaultData)
        );

        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


    // ***************** Login Business Logic Module END *****************
}
