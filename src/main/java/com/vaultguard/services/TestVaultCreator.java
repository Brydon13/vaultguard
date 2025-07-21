package com.vaultguard.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestVaultCreator {
    public static void main(String[] args) throws Exception {
        String username = "testuser";
        String password = "CorrectPassword123";

        // Generate salt
        UserService userService = new UserService();
        byte[] salt = userService.generateSalt();

        // Encrypt a dummy string with password+salt for the "auth" key
        EncryptionService encryptionService = new EncryptionService();
        EncryptionService.EncryptedData ed = encryptionService.encrypt("dummy", password, salt);

        // Prepare JSON structure
        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        Map<String, String> authKey = new HashMap<>();
        authKey.put("iv", ed.getIv());
        authKey.put("ciphertext", ed.getCiphertext());

        Map<String, Object> keys = new HashMap<>();
        keys.put("vaultguard-auth", authKey);

        Map<String, Object> vaultData = new HashMap<>();
        vaultData.put("salt", saltBase64);
        vaultData.put("keys", keys);

        // Write to vaults/testuser.json
        Path vaultDir = Paths.get("vaults");
        Files.createDirectories(vaultDir);

        ObjectMapper mapper = new ObjectMapper();
        Files.write(
          vaultDir.resolve(username + ".json"),
          mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(vaultData)
        );

        System.out.println("Test vault for 'testuser' created!");
    }
}
