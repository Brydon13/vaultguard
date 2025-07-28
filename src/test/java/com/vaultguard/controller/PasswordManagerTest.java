package com.vaultguard.controller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.vaultguard.services.UserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class PasswordManagerTest {

    private PasswordManager pm;
    private static final String TEST_VAULT_PATH = "./test-vaults";

    @BeforeEach
    public void setup() throws Exception {
        pm = new PasswordManager(TEST_VAULT_PATH);
    }

    @AfterEach
    public void cleanup() throws IOException {
        Path directory = Paths.get(TEST_VAULT_PATH);
        if (Files.exists(directory)) {
            deleteDirectoryRecursively(directory);
        }
    }

    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }

    // -------- TDD Tests --------

    @Test
    void testRegister_Success() throws Exception {
        assertTrue(pm.register("testuser", "CorrectPassword123"));
    }

    @Test
    void testRegister_DuplicateUsername_Fails() throws Exception {
        assertTrue(pm.register("testuser", "Password123"));
        assertFalse(pm.register("testuser", "Password123"));
    }

    @Test
    void testRegister_InvalidUsername_Fails() throws Exception {
        assertFalse(pm.register("", "Password123"));
        assertFalse(pm.register(null, "Password123"));
    }

    @Test
    void testLogin_AfterRegister_Success() throws Exception {
        String username = "authuser";
        String password = "RegisTest123";

        assertTrue(pm.register(username, password));
        assertTrue(pm.login(username, password));
    }

    @Test
    void testLogin_WrongPassword_Fails() throws Exception {
        String username = "testuser2";
        String password = "RightPass123";
        String wrongPassword = "WrongPass123";

        assertTrue(pm.register(username, password));
        assertFalse(pm.login(username, wrongPassword));
    }

    @Test
    void testLogin_NonExistentUser_Fails() throws Exception {
        assertFalse(pm.login("nonexistent", "somepassword"));
    }

    @Test
    void testAddGetEditDeleteKey() throws Exception {
        String username = "testuser";
        String password = "Password123";

        assertTrue(pm.register(username, password));
        assertTrue(pm.login(username, password));

        assertTrue(pm.addKey("myKey", "mySecretValue"));

        List<String> keys = pm.getVaultKeyNames();
        assertTrue(keys.size() == 1);
        assertTrue(keys.contains("myKey"));

        String value = pm.getKeyValue("myKey");
        assertEquals("mySecretValue", value);

        assertTrue(pm.editKey("myKey", "newSecretValue"));
        String editedValue = pm.getKeyValue("myKey");
        assertEquals("newSecretValue", editedValue);

        assertTrue(pm.deleteKey("myKey"));
        keys = pm.getVaultKeyNames();
        assertTrue(keys.size() == 0);
        assertFalse(pm.getVaultKeyNames().contains("myKey"));
    }

    @Test
    void testAddKey_InvalidNameOrValue_Fails() throws Exception {
        String username = "testuser";
        String password = "Password123";

        assertTrue(pm.register(username, password));
        assertTrue(pm.login(username, password));

        assertFalse(pm.addKey(null, "value"));
        assertFalse(pm.addKey("", "value"));
        assertFalse(pm.addKey("vaultguard-auth", "value"));

        assertFalse(pm.addKey("validKey", null));
        assertFalse(pm.addKey("validKey", ""));
        assertFalse(pm.addKey("validKey", "   "));
    }

    @Test
    void testEditKey_NonExistentKey_Fails() throws Exception {
        String username = "testuser";
        String password = "Password123";

        assertTrue(pm.register(username, password));
        assertTrue(pm.login(username, password));

        assertFalse(pm.editKey("noKey", "someValue"));
    }

    @Test
    void testGetKeyValue_AuthKeyName_ReturnsNull() throws Exception {
        String username = "testuser";
        String password = "Password123";

        assertTrue(pm.register(username, password));
        assertTrue(pm.login(username, password));

        assertNull(pm.getKeyValue("vaultguard-auth"));
    }

    @Test
    void testLogout_ClearsActiveUser() throws Exception {
        String username = "testuser";
        String password = "Password123";

        assertTrue(pm.register(username, password));
        assertTrue(pm.login(username, password));
        assertTrue(pm.addKey("myKey", "mySecretValue"));

        pm.logout();

        assertTrue(pm.getVaultKeyNames().isEmpty());
        assertNull(pm.getKeyValue("someKey"));
        assertFalse(pm.addKey("someKey", "someValue"));
    }

    @Test
    void testGenerateStrongPassword_Length() {
        String password = pm.generateStrongPassword();
        assertNotNull(password);
        assertEquals(16, password.length());
    }

    // -------- Structure-based Testing --------
    @Test
    void testRegister_PathTesting_1_3_5_6() throws Exception {
        //Force an exception to be thrown
        UserService mockUserService = mock(UserService.class);
        when(mockUserService.validateUsernameAndPassword(anyString(), anyString())).thenReturn(true);
        when(mockUserService.generateSalt()).thenThrow(new RuntimeException("Forced exception"));

        PasswordManager pmWithMockUserService = new PasswordManager(TEST_VAULT_PATH, mockUserService, null, null);

        boolean result = pmWithMockUserService.register("testuser", "Password123");

        assertFalse(result);
    }

    @Test
    void testRegister_PathTesting_1_3_5_7() throws Exception {
        assertTrue(pm.register("testuser", "Password123"));
    }

    @Test
    void testRegister_PathTesting_1_3_4() throws Exception {
        assertTrue(pm.register("testuser", "Password123"));
        assertFalse(pm.register("testuser", "Password123"));
    }

    @Test
    void testRegister_PathTesting_1_2() throws Exception {
        assertFalse(pm.register("", "Password123"));
    }

    // -------- Integration Testing --------

    @Test //Covers untested DU Pairs: 1, 3, 10
    void testRegister_Integration_InvalidUsernameAndPassword() throws Exception {
        assertFalse(pm.register("", ""));
    }

    @Test //Covers untested DU Pairs: 5, 7, 12
    void testLogin_Integration_InvalidUsernameAndPassword() throws Exception {
        assertFalse(pm.login("", ""));
    }

    @Test //Covers untested DU Pairs: 2, 4, 9, 6, 8, 11
    void testRegisterAndLogin_Integration_ValidUsernameAndPassword() throws Exception {
        assertTrue(pm.register("validuser", "validpass"));
        assertTrue(pm.login("validuser", "validpass"));
    }

}
