package com.vaultguard.services;

import com.vaultguard.model.EncryptedData;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;

public class EncryptionService {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BIT = 128;
    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 100_000;

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";

    public SecretKey deriveKey(String password, byte[] salt) throws GeneralSecurityException{
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public EncryptedData encrypt(String message, SecretKey key) throws GeneralSecurityException {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
        byte[] ciphertextBytes = cipher.doFinal(message.getBytes());

        return new EncryptedData(
            Base64.getEncoder().encodeToString(iv),
            Base64.getEncoder().encodeToString(ciphertextBytes)
        );
    }

    public String decrypt(EncryptedData encryptedData, SecretKey key) throws GeneralSecurityException {
        byte[] iv = Base64.getDecoder().decode(encryptedData.getIv());
        byte[] ciphertext = Base64.getDecoder().decode(encryptedData.getCiphertext());
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
        byte[] plaintextBytes = cipher.doFinal(ciphertext);

        return new String(plaintextBytes);
    }
}

