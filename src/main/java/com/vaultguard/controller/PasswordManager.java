package com.vaultguard.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;

import com.vaultguard.model.EncryptedData;
import com.vaultguard.model.VaultEntry;
import com.vaultguard.model.VaultFile;
import com.vaultguard.services.EncryptionService;
import com.vaultguard.services.PasswordGenerationService;
import com.vaultguard.services.StorageService;
import com.vaultguard.services.UserService;

public class PasswordManager {

    private static final String AUTH_KEY_NAME = "vaultguard-auth";
    private static final int GENERATED_PASSWORD_LENGTH = 16;

    private static final int MIN_KEY_NAME_LENGTH = 1;
    private static final int MAX_KEY_NAME_LENGTH = 64;
    private static final String KEY_NAME_PATTERN = "^[A-Za-z0-9_\\- ]+$";

    private static final int MIN_KEY_VALUE_LENGTH = 1;
    private static final int MAX_KEY_VALUE_LENGTH = 1024;

    private UserService userService = new UserService();
    private EncryptionService encryptionService = new EncryptionService();
    private StorageService storageService;
    private PasswordGenerationService passwordGenerationService = new PasswordGenerationService();

    private SecretKey activeEncryptionKey = null;
    private String activeUsername = null;

    public PasswordManager(String storagePath) {
        this.storageService = new StorageService(storagePath);
    }

    /**
     * Attempts to register a new user
     *
     * @return true if registration successful, false otherwise
     */
    public boolean register(String username, String password) throws Exception {
        if (!userService.validateUsernameAndPassword(username, password)) return false;

        VaultFile vault = storageService.loadVault(username);
        if (vault != null) return false; //username already exists

        try {
            byte[] salt = userService.generateSalt();
            SecretKey tempKey = encryptionService.deriveKey(password, salt);

            EncryptedData encryptedAuthKey = encryptionService.encrypt("dummy", tempKey);

            VaultEntry authVaultEntry = new VaultEntry(AUTH_KEY_NAME, encryptedAuthKey);
            List<VaultEntry> keys = new ArrayList<>();
            keys.add(authVaultEntry);

            String saltString = Base64.getEncoder().encodeToString(salt);

            VaultFile vaultFile = new VaultFile(saltString, keys);

            storageService.saveVault(username, vaultFile);

            //Log user in after registration
            activeEncryptionKey = tempKey;
            activeUsername = username;

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Attempts to login the user by checking their credentials
     * against their vault file.
     *
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) throws Exception {
        if (!userService.validateUsernameAndPassword(username, password)) return false;

        VaultFile vault = storageService.loadVault(username);
        if (vault == null) return false; //username does not exist

        byte[] salt = Base64.getDecoder().decode(vault.salt);
        SecretKey tempKey = encryptionService.deriveKey(password, salt);

        EncryptedData authKeyData = null;

        for (VaultEntry vaultEntry : vault.keys) {
            if (vaultEntry.name.equals(AUTH_KEY_NAME)) {
                authKeyData = vaultEntry.encryptedKey;
                break;
            }
        }

        //Auth key does not exist (should never occur)
        if (authKeyData == null) return false;

        //Attempt to decrypt the auth key with the derived key. 
        //If decrpytion fails it will throw an exception.
        try {
            encryptionService.decrypt(authKeyData, tempKey);
            activeEncryptionKey = tempKey;
            activeUsername = username;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Logs out the current user
     *
     * @return void
     */
    public void logout() {
        activeEncryptionKey = null;
        activeUsername = null;
    }

    /**
     * Returns a list of vault key names for the logged in user
     *
     * @return List<String>
     */
    public List<String> getVaultKeyNames() throws Exception {
        List<String> keyNames = new ArrayList<>();
        if (activeEncryptionKey == null || activeUsername == null) return keyNames;

        VaultFile vault = storageService.loadVault(activeUsername);
        if (vault == null) return keyNames;

        for (VaultEntry vaultEntry : vault.keys) {
            if (!vaultEntry.name.equals(AUTH_KEY_NAME)) {
                keyNames.add(vaultEntry.name);
            }
        }

        return keyNames;
    }

    /**
     * Returns the decrypted value of a key in the vault
     *
     * @return String
     */
    public String getKeyValue(String keyName) throws Exception {
        if (keyName.equals(AUTH_KEY_NAME)) return null;
        if (activeEncryptionKey == null || activeUsername == null) return null;

        VaultFile vault = storageService.loadVault(activeUsername);
        if (vault == null) return null;

        for (VaultEntry vaultEntry : vault.keys) {
            if (vaultEntry.name.equals(keyName)) {
                try {
                    String decryptedValue = encryptionService.decrypt(vaultEntry.encryptedKey, activeEncryptionKey);
                    return decryptedValue;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Generates a strong unique password for the user
     *
     * @return String
     */
    public String generateStrongPassword() {
        return passwordGenerationService.generatePassword(GENERATED_PASSWORD_LENGTH);
    }

    /**
     * Validates key name
     *
     * @return true if valid, false otherwise
     */
    private boolean isValidKeyName(String keyName) {
        if (keyName == null) return false;
        if (keyName.equals(AUTH_KEY_NAME)) return false;
        if (keyName.length() < MIN_KEY_NAME_LENGTH || keyName.length() > MAX_KEY_NAME_LENGTH) return false;
        if (!keyName.matches(KEY_NAME_PATTERN)) return false;
        if (keyName.trim().isEmpty()) return false;
        return true;
    }

    /**
     * Validates key value
     *
     * @return true if valid, false otherwise
     */
    private boolean isValidKeyValue(String keyValue) {
        if (keyValue == null) return false;
        if (keyValue.length() < MIN_KEY_VALUE_LENGTH || keyValue.length() > MAX_KEY_VALUE_LENGTH) return false;
        if (keyValue.trim().isEmpty()) return false;
        return true;
    }

    /**
     * Adds a new key to the active user's vault
     *
     * @return true if added, false if error
     */
    public boolean addKey(String name, String value) throws Exception {
        if (activeEncryptionKey == null || activeUsername == null) return false;
        if(!isValidKeyName(name) || !isValidKeyValue(value)) return false;

        VaultFile vault = storageService.loadVault(activeUsername);
        if (vault == null) return false;

        for (VaultEntry vaultEntry : vault.keys) {
            if (vaultEntry.name.equals(name)) {
                return false; //Already exists
            }
        }

        EncryptedData encryptedValue = encryptionService.encrypt(value, activeEncryptionKey);
        VaultEntry newVaultEntry = new VaultEntry(name, encryptedValue);
        vault.keys.add(newVaultEntry);

        storageService.saveVault(activeUsername, vault);
        return true;
    }

    /**
     * Edits an existing key in the active user's vault
     *
     * @return true if edited, false if error
     */
    public boolean editKey(String name, String newValue) throws Exception {
        if (activeEncryptionKey == null || activeUsername == null) return false;
        if(!isValidKeyName(name) || !isValidKeyValue(newValue)) return false;

        VaultFile vault = storageService.loadVault(activeUsername);
        if (vault == null) return false;

        List<VaultEntry> keys = vault.keys;

        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).name.equals(name)) {
                EncryptedData encryptedValue = encryptionService.encrypt(newValue, activeEncryptionKey);
                keys.set(i, new VaultEntry(name, encryptedValue));
                storageService.saveVault(activeUsername, vault);
                return true;
            }
        }

        return false;
    }

    /**
     * Deletes a key by name from the active user's vault.
     *
     * @return true if deleted, false if error.
     */
    public boolean deleteKey(String name) throws Exception {
        if (activeEncryptionKey == null || activeUsername == null) return false;
        if (!isValidKeyName(name)) return false;

        VaultFile vault = storageService.loadVault(activeUsername);
        if (vault == null) return false;

        List<VaultEntry> keys = vault.keys;

        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).name.equals(name)) {
                keys.remove(i);
                storageService.saveVault(activeUsername, vault);
                return true;
            }
        }

        return false;
    }
}
