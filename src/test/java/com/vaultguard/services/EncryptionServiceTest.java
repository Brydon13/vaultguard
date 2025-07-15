package com.vaultguard.services;

import com.vaultguard.model.EncryptedData;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.security.SecureRandom;

public class EncryptionServiceTest {

    EncryptionService service = new EncryptionService();

    byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    @Test
    void testEncryptThenDecrypt() throws Exception {
        String message = "Test message";
        String password = "testPassword";
        byte[] salt = generateSalt();

        var encryptedMessage = service.encrypt(message, password, salt);
        String decryptedMessage = service.decrypt(encryptedMessage, password, salt);

        assertEquals(message, decryptedMessage);
    }

    @Test
    void testEncryptionIsAlwaysUnique() throws Exception {
        String message = "Test message";
        String password = "testPassword";
        byte[] salt = generateSalt();

        var encrypted1 = service.encrypt(message, password, salt);
        var encrypted2 = service.encrypt(message, password, salt);

        assertNotEquals(encrypted1.getCiphertext(), encrypted2.getCiphertext());
        assertNotEquals(encrypted1.getIv(), encrypted2.getIv());
    }

    @Test
    void testDecryptionWithWrongPasswordFails() throws Exception {
        String message = "Test message";
        String password = "testPassword";
        byte[] salt = generateSalt();

        var encrypted = service.encrypt(message, password, salt);

        String wrongPassword = "wrong";
        assertThrows(Exception.class, () -> {
            service.decrypt(encrypted, wrongPassword, salt);
        });
    }

    @Test
    void testDecryptionWithWrongIVFails() throws Exception {
        String message = "Test message";
        String password = "testPassword";
        byte[] salt = generateSalt();

        var encrypted = service.encrypt(message, password, salt);

        byte[] iv = java.util.Base64.getDecoder().decode(encrypted.getIv());
        iv[0] ^= 1; //flip a bit
        String newIv = java.util.Base64.getEncoder().encodeToString(iv);
        var newEncrypted = new EncryptedData(newIv, encrypted.getCiphertext());

        assertThrows(Exception.class, () -> {
            service.decrypt(newEncrypted, password, salt);
        });
    }
}

