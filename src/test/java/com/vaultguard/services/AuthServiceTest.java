package com.vaultguard.services;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    // ***************** Login Tests BEGIN *****************

    // Positive Tests

    @Test // Login with correct credentials (existing vault)
    void testLogin_CorrectCredentials_Success() {
        AuthService auth = new AuthService();
        assertTrue(auth.login("testuser", "CorrectPassword123"));
    }

    @Test // Login with minimum length username and password (needs corresponding vault file)
    void testLogin_MinLengthUsernamePassword_Success() {
        AuthService auth = new AuthService();
        // Setup: Register a new user with min length, or run TestVaultCreator with these values first
        String username = "user1234"; // 8 chars
        String password = "passwrd8"; // 8 chars
        if (!Files.exists(Paths.get("vaults", username + ".json"))) {
            auth.registerNewUser(username, password);
        }
        assertTrue(auth.login(username, password));
    }

    @Test // Login with maximum length username and password (needs corresponding vault file)
    void testLogin_MaxLengthUsernamePassword_Success() {
        AuthService auth = new AuthService();
        String username = "u".repeat(32); // 32 chars
        String password = "p".repeat(32); // 32 chars
        if (!Files.exists(Paths.get("vaults", username + ".json"))) {
            auth.registerNewUser(username, password);
        }
        assertTrue(auth.login(username, password));
    }

    @Test // Login with username with spaces and underscores (needs corresponding vault file)
    void testLogin_UsernameWithSpacesAndUnderscore_Success() {
        AuthService auth = new AuthService();
        String username = "user name_1";
        String password = "Password123!";
        if (!Files.exists(Paths.get("vaults", username + ".json"))) {
            auth.registerNewUser(username, password);
        }
        assertTrue(auth.login(username, password));
    }

    @Test // Login with username containing only numbers (needs corresponding vault file)
    void testLogin_UsernameWithNumbersOnly_Success() {
        AuthService auth = new AuthService();
        String username = "12345678";
        String password = "PassNum12";
        if (!Files.exists(Paths.get("vaults", username + ".json"))) {
            auth.registerNewUser(username, password);
        }
        assertTrue(auth.login(username, password));
    }

    @Test // Login after registering a new user
    void testLogin_AfterRegisterNewUser_Success() {
        AuthService auth = new AuthService();
        String username = "authuser";
        String password = "RegisTest123";
        // Register
        assertTrue(auth.registerNewUser(username, password));
        // Now login
        assertTrue(auth.login(username, password));
    }

    // Negative Tests

    @Test // Login with empty username
    void testLogin_EmptyUsername_Fails() {
        AuthService auth = new AuthService();
        assertFalse(auth.login("", "anyPassword123"));
    }

    @Test // Login with empty password
    void testLogin_EmptyPassword_Fails() {
        AuthService auth = new AuthService();
        assertFalse(auth.login("testuser", ""));
    }

    @Test // Login with null username
    void testLogin_NullUsername_Fails() {
        AuthService auth = new AuthService();
        assertFalse(auth.login(null, "anyPassword123"));
    }

    @Test // Login with null password
    void testLogin_NullPassword_Fails() {
        AuthService auth = new AuthService();
        assertFalse(auth.login("testuser", null));
    }

    @Test // Login with username with illegal characters
    void testLogin_UsernameWithIllegalCharacters_Fails() {
        AuthService auth = new AuthService();
        assertFalse(auth.login("bad!user", "ValidPassword1!"));
    }

    @Test // Login with username with only spaces
    void testLogin_UsernameWithOnlySpaces_Fails() {
        AuthService auth = new AuthService();
        assertFalse(auth.login("        ", "ValidPassword1!"));
    }

    @Test // Login with password containing spaces
    void testLogin_PasswordWithSpaces_Fails() {
        AuthService auth = new AuthService();
        assertFalse(auth.login("testuser", "Wrong Password!"));
    }

    // ***************** Login Tests END *****************
}
