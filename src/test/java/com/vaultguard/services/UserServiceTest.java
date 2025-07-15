package com.vaultguard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    // ***************** Registration Tests BEGIN *****************

    // Positive tests
    @Test // Register new user with unique username
    void testRegisterNewUser_Success() {
        UserService userService = new UserService();
        boolean result = userService.register("alice_01", "MySuperSecret123!");
        assertTrue(result, "Registration should succeed for a new user");
    }

    @Test // Register user with strong password (special symbols, numbers, mixed case)
    void testRegisterStrongPassword_Success() {
        UserService userService = new UserService();
        boolean result = userService.register("bob_strong", "P@$$w0rd!@#");
        assertTrue(result, "Registration should succeed with strong password");
    }

    @Test // Register user with spaces and underscores in username (allowed)
    void testRegisterUsernameWithSpacesAndUnderscore_Success() {
        UserService userService = new UserService();
        boolean result = userService.register("user name_1", "Pass12345!");
        assertTrue(result, "Should allow spaces and underscores in username");
    }

    @Test // Register username with numbers (allowed)
    void testRegisterUsernameWithNumbersOnly_Success() {
        UserService userService = new UserService();
        boolean result = userService.register("user123456", "Password9!");
        assertTrue(result, "Should allow numbers in username");
    }

    @Test // Register user with maximum allowed username and password length
    void testRegisterMaxLengthUsernamePassword_Success() {
        UserService userService = new UserService();
        String username = "u".repeat(32); // 32 chars
        String password = "p".repeat(32); // 32 chars
        boolean result = userService.register(username, password);
        assertTrue(result, "Registration should succeed with max-length username/password");
    }

    @Test // Register user with minimum allowed username and password length
    void testRegisterMinLengthUsernamePassword_Success() {
        UserService userService = new UserService();
        String username = "user1234"; // 8 chars
        String password = "passwrd8"; // 8 chars
        boolean result = userService.register(username, password);
        assertTrue(result, "Registration should succeed with min-length username/password");
    }

    // Negative tests
    @Test // Register duplicate user
    void testRegisterDuplicateUser_Fails() {
        UserService userService = new UserService();
        userService.register("bob_02", "Password1!");
        boolean result = userService.register("bob_02", "Password2!");
        assertFalse(result, "Duplicate username registration should fail");
    }

    @Test // Register user with empty username
    void testRegisterEmptyUsername_Fails() {
        UserService userService = new UserService();
        boolean result = userService.register("", "password123");
        assertFalse(result, "Empty username should not be allowed");
    }

    @Test // Register user with empty password
    void testRegisterEmptyPassword_Fails() {
        UserService userService = new UserService();
        boolean result = userService.register("charlie_01", "");
        assertFalse(result, "Empty password should not be allowed");
    }

    @Test // Register user with null username or null password
    void testRegisterNullUsernameOrPassword_Fails() {
        UserService userService = new UserService();
        boolean nullUsername = userService.register(null, "password123");
        boolean nullPassword = userService.register("dave_01", null);
        assertFalse(nullUsername, "Null username should not be allowed");
        assertFalse(nullPassword, "Null password should not be allowed");
    }

    @Test // Register username too short (<8 chars)
    void testRegisterUsernameTooShort_Fails() {
        UserService userService = new UserService();
        boolean result = userService.register("usr1", "Password9!");
        assertFalse(result, "Username less than 8 chars should fail");
    }

    @Test // Register username too long (>32 chars)
    void testRegisterUsernameTooLong_Fails() {
        UserService userService = new UserService();
        String uname = "a".repeat(33);
        boolean result = userService.register(uname, "Password9!");
        assertFalse(result, "Username longer than 32 chars should fail");
    }

    @Test // Register password too short (<8 chars)
    void testRegisterPasswordTooShort_Fails() {
        UserService userService = new UserService();
        boolean result = userService.register("username1", "pass12");
        assertFalse(result, "Password less than 8 chars should fail");
    }

    @Test // Register password too long (>32 chars)
    void testRegisterPasswordTooLong_Fails() {
        UserService userService = new UserService();
        String pwd = "a".repeat(33);
        boolean result = userService.register("username1", pwd);
        assertFalse(result, "Password longer than 32 chars should fail");
    }
    
    @Test // Register password with spaces
    void testRegisterPasswordWithSpaces_Fails() {
        UserService userService = new UserService();
        boolean result = userService.register("username1", "Password 1!");
        assertFalse(result, "Password with spaces should fail");
    }

    @Test // Register username with only spaces
    void testRegisterUsernameOnlySpaces_Fails() {
        UserService userService = new UserService();
        boolean result = userService.register("        ", "Password9!");
        assertFalse(result, "Username with only spaces should fail");
    }

    // ***************** Registration Tests END *****************
}
