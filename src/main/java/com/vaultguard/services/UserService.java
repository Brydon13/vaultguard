package com.vaultguard.services;

import java.security.SecureRandom;

public class UserService {

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 32;
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int MAX_PASSWORD_LENGTH = 32;

    // Only letters, numbers, underscores, or spaces allowed
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_ ]+$";

    /**
     * Validates username and password for registration and login.
     * Username: 8-32 chars, only letters/numbers/underscore/space, not only spaces.
     * Password: 8-32 chars, no spaces.
     * Returns true if valid, false if invalid.
     */
    public boolean validateUsernameAndPassword(String username, String password) {
        boolean result = true;
        if (username == null ||
            password == null ||
            username.length() < MIN_USERNAME_LENGTH || 
            username.length() > MAX_USERNAME_LENGTH ||
            password.length() < MIN_PASSWORD_LENGTH ||
            password.length() > MAX_PASSWORD_LENGTH ||
            !username.matches(USERNAME_PATTERN) ||
            username.trim().isEmpty() ||
            password.contains(" ")) result = false;

        return result;
    }

    /**
     * Generates a new random salt (16 bytes).
     */
    public byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
