package com.vaultguard.services;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private static final int MIN_USERNAME_LENGTH = 8;
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 32;

    // Only letters, numbers, underscores, or spaces (at least 1 non-space)
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_ ]+$";

    // Map to store users
    private Map<String, UserRecord> users = new HashMap<>();
    private EncryptionService encryptionService = new EncryptionService();

    // ***************** Registration Module BEGIN *****************
    /**
     * Registers a new user with username and password.
     * Username: 8-32 chars, only letters/numbers/underscore/space, not only spaces
     * Password: 8-32 chars, no spaces
     * Returns false if input is invalid or username taken.
     */
    public boolean register(String username, String password) {
        // Null check
        if (username == null || password == null) return false;

        // Length checks
        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) return false;
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) return false;

        // Username pattern and only spaces check
        if (!username.matches(USERNAME_PATTERN)) return false;
        if (username.trim().isEmpty()) return false;

        // No spaces allowed in password
        if (password.contains(" ")) return false;

        // Username already exists
        if (users.containsKey(username)) return false;

        try {
            byte[] salt = new byte[16];
            new java.security.SecureRandom().nextBytes(salt);
            EncryptionService.EncryptedData encrypted = encryptionService.encrypt(password, password, salt);
            users.put(username, new UserRecord(encrypted, salt));
            return true;
        } catch (Exception e) {
            // In production, log the error
            return false;
        }
    }
    // ***************** Registration Module END *****************

    // ***************** Login Module BEGIN *****************
    // (Add your login method here in the future)
    // ***************** Login Module END *****************

    // ***************** UserRecord Class BEGIN *****************
    private static class UserRecord {
        EncryptionService.EncryptedData encryptedPassword;
        byte[] salt;
        UserRecord(EncryptionService.EncryptedData encryptedPassword, byte[] salt) {
            this.encryptedPassword = encryptedPassword;
            this.salt = salt;
        }
    }
    // ***************** UserRecord Class END *****************
}
