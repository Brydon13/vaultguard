package com.vaultguard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    // ***************** Registration Validation Tests BEGIN *****************

    // Positive tests
    @Test // Valid username and password
    void testValidateRegistration_Valid_Success() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("alice_01", "MySuperSecret123!");
        assertTrue(result, "Validation should succeed for a valid username and password");
    }

    @Test // Username with spaces and underscores, valid password
    void testValidateRegistration_UsernameWithSpacesAndUnderscore_Success() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("user name_1", "Pass12345!");
        assertTrue(result, "Should allow spaces and underscores in username");
    }

    @Test // Username with numbers only, valid password
    void testValidateRegistration_UsernameWithNumbersOnly_Success() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("user123456", "Password9!");
        assertTrue(result, "Should allow numbers in username");
    }

    @Test // Maximum allowed username and password length
    void testValidateRegistration_MaxLengthUsernamePassword_Success() {
        UserService userService = new UserService();
        String username = "u".repeat(32); // 32 chars
        String password = "p".repeat(32); // 32 chars
        boolean result = userService.validateRegistration(username, password);
        assertTrue(result, "Should succeed with max-length username/password");
    }

    @Test // Minimum allowed username and password length
    void testValidateRegistration_MinLengthUsernamePassword_Success() {
        UserService userService = new UserService();
        String username = "user1234"; // 8 chars
        String password = "passwrd8"; // 8 chars
        boolean result = userService.validateRegistration(username, password);
        assertTrue(result, "Should succeed with min-length username/password");
    }

    // Negative tests
    @Test // Empty username
    void testValidateRegistration_EmptyUsername_Fails() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("", "password123");
        assertFalse(result, "Empty username should not be allowed");
    }

    @Test // Empty password
    void testValidateRegistration_EmptyPassword_Fails() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("charlie_01", "");
        assertFalse(result, "Empty password should not be allowed");
    }

    @Test // Null username or null password
    void testValidateRegistration_NullUsernameOrPassword_Fails() {
        UserService userService = new UserService();
        boolean nullUsername = userService.validateRegistration(null, "password123");
        boolean nullPassword = userService.validateRegistration("dave_01", null);
        assertFalse(nullUsername, "Null username should not be allowed");
        assertFalse(nullPassword, "Null password should not be allowed");
    }

    @Test // Username too short (<8 chars)
    void testValidateRegistration_UsernameTooShort_Fails() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("usr1", "Password9!");
        assertFalse(result, "Username less than 8 chars should fail");
    }

    @Test // Username too long (>32 chars)
    void testValidateRegistration_UsernameTooLong_Fails() {
        UserService userService = new UserService();
        String uname = "a".repeat(33);
        boolean result = userService.validateRegistration(uname, "Password9!");
        assertFalse(result, "Username longer than 32 chars should fail");
    }

    @Test // Password too short (<8 chars)
    void testValidateRegistration_PasswordTooShort_Fails() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("username1", "pass12");
        assertFalse(result, "Password less than 8 chars should fail");
    }

    @Test // Password too long (>32 chars)
    void testValidateRegistration_PasswordTooLong_Fails() {
        UserService userService = new UserService();
        String pwd = "a".repeat(33);
        boolean result = userService.validateRegistration("username1", pwd);
        assertFalse(result, "Password longer than 32 chars should fail");
    }
    
    @Test // Password with spaces
    void testValidateRegistration_PasswordWithSpaces_Fails() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("username1", "Password 1!");
        assertFalse(result, "Password with spaces should fail");
    }

    @Test // Username with only spaces
    void testValidateRegistration_UsernameOnlySpaces_Fails() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("        ", "Password9!");
        assertFalse(result, "Username with only spaces should fail");
    }

    @Test // Username with illegal characters
    void testValidateRegistration_UsernameWithIllegalCharacters_Fails() {
        UserService userService = new UserService();
        boolean result = userService.validateRegistration("user@name", "Password1!");
        assertFalse(result, "Username with illegal character (@) should fail");
    }

    // ***************** Registration Validation Tests END *****************

    // ***************** Salt Generation Tests BEGIN *****************
    @Test // Salt generation should return 16-byte arrays, not equal for two calls
    void testGenerateSalt_CorrectLengthAndRandom() {
        UserService userService = new UserService();
        byte[] salt1 = userService.generateSalt();
        byte[] salt2 = userService.generateSalt();
        assertEquals(16, salt1.length, "Salt should be 16 bytes");
        assertEquals(16, salt2.length, "Salt should be 16 bytes");
        // It's highly unlikely two random salts would be equal
        assertNotEquals(java.util.Arrays.toString(salt1), java.util.Arrays.toString(salt2), "Salts should not be equal");
    }
    // ***************** Salt Generation Tests END *****************
}
