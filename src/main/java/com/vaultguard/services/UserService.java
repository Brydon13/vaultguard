package com.vaultguard.services;

import java.security.SecureRandom;

public class UserService {

    private static final int MIN_USERNAME_LENGTH = 8;
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 32;

    // Only letters, numbers, underscores, or spaces allowed
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_ ]+$";

    // ***************** Registration Validation Module BEGIN *****************

    /**
     * Validates username and password for registration.
     * Username: 8-32 chars, only letters/numbers/underscore/space, not only spaces.
     * Password: 8-32 chars, no spaces.
     * Returns true if valid, false if invalid.
     */
    public boolean validateRegistration(String username, String password) {
        if (username == null || password == null) return false;

        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) return false;
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) return false;

        if (!username.matches(USERNAME_PATTERN)) return false;
        if (username.trim().isEmpty()) return false;

        if (password.contains(" ")) return false;

        return true;
    }

    // ***************** Registration Validation Module END *****************

    // ***************** Salt Generation Module BEGIN *****************

    /**
     * Generates a new random salt (16 bytes).
     */
    public byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // ***************** Salt Generation Module END *****************

    // ***************** Login Validation Module BEGIN *****************
    /**
     * Validates username and password for login.
     * Uses the same checks as registration.
     */
    public boolean validateLogin(String username, String password) {
        return validateRegistration(username, password);
    }
    // ***************** Login Validation Module END *****************
}
