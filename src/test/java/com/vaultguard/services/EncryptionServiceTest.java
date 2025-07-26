package com.vaultguard.services;

import com.vaultguard.model.EncryptedData;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.security.SecureRandom;

import javax.crypto.SecretKey;

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

        SecretKey key = service.deriveKey(password, salt);

        var encryptedMessage = service.encrypt(message, key);
        String decryptedMessage = service.decrypt(encryptedMessage, key);

        assertEquals(message, decryptedMessage);
    }

    @Test
    void testEncryptionIsAlwaysUnique() throws Exception {
        String message = "Test message";
        String password = "testPassword";
        byte[] salt = generateSalt();

        SecretKey key = service.deriveKey(password, salt);

        var encrypted1 = service.encrypt(message, key);
        var encrypted2 = service.encrypt(message, key);

        assertNotEquals(encrypted1.getCiphertext(), encrypted2.getCiphertext());
        assertNotEquals(encrypted1.getIv(), encrypted2.getIv());
    }

    @Test
    void testDecryptionWithWrongPasswordFails() throws Exception {
        String message = "Test message";
        String password = "testPassword";
        byte[] salt = generateSalt();

        SecretKey key = service.deriveKey(password, salt);

        var encrypted = service.encrypt(message, key);

        String wrongPassword = "wrong";

        SecretKey wrongKey = service.deriveKey(wrongPassword, salt);

        assertThrows(Exception.class, () -> {
            service.decrypt(encrypted, wrongKey);
        });
    }

    @Test
    void testDecryptionWithWrongIVFails() throws Exception {
        String message = "Test message";
        String password = "testPassword";
        byte[] salt = generateSalt();

        SecretKey key = service.deriveKey(password, salt);

        var encrypted = service.encrypt(message, key);

        byte[] iv = java.util.Base64.getDecoder().decode(encrypted.getIv());
        iv[0] ^= 1; //flip a bit
        String newIv = java.util.Base64.getEncoder().encodeToString(iv);
        var newEncrypted = new EncryptedData(newIv, encrypted.getCiphertext());

        assertThrows(Exception.class, () -> {
            service.decrypt(newEncrypted, key);
        });
    }
}

