package com.vaultguard.services;

import com.vaultguard.model.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {

    private static final String TEST_DIR = "test_vaults";
    private StorageService storageService;

    @BeforeEach
    void setup() throws Exception {
        Files.createDirectories(Paths.get(TEST_DIR));
        storageService = new StorageService(TEST_DIR);
    }

    @AfterEach
    void cleanup() throws Exception {
        Path dir = Paths.get(TEST_DIR);
        if (Files.exists(dir)) {
            Files.walk(dir)
                .map(Path::toFile)
                .forEach(File::delete);
            Files.deleteIfExists(dir);
        }
    }

    @Test
    void testSaveAndLoadVault() throws Exception {
        String username = "testUser";

        EncryptedData encryptedData = new EncryptedData("testIv", "testCipher");
        VaultEntry vaultEntry = new VaultEntry("facebook password", encryptedData);
        List<VaultEntry> keys = new ArrayList<>();
        keys.add(vaultEntry);
        VaultFile vaultFile = new VaultFile("testSalt", keys);

        storageService.saveVault(username, vaultFile);

        VaultFile loadedVault = storageService.loadVault(username);

        assertEquals("testSalt", loadedVault.salt);
        assertEquals(1, loadedVault.keys.size());
        assertEquals("facebook password", loadedVault.keys.get(0).name);
        assertEquals("testIv", loadedVault.keys.get(0).encryptedKey.getIv());
        assertEquals("testCipher", loadedVault.keys.get(0).encryptedKey.getCiphertext());
    }

    @Test
    void testNonExistentLoadVaultReturnsNull() throws Exception {
        VaultFile loadedVault = storageService.loadVault("testUser");
        assertNull(loadedVault);
    }
}
