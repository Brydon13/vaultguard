package com.vaultguard.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionServiceTest {

    @Test
    public void test() {
        EncryptionService service = new EncryptionService();
        String result = service.testSetup("blah");

        assertEquals("test blah", result);
    }
}
