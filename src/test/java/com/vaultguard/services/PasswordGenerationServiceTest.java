package com.vaultguard.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordGenerationServiceTest {
    @Test
    public void PasswordLength() {
        PasswordGenerationService gen = new PasswordGenerationService();
        String password = gen.generatePassword(12);
        assertEquals(12, password.length());
    }

    @Test
    public void ContainsUppercase() {
        PasswordGenerationService gen = new PasswordGenerationService();
        String password = gen.generatePassword(12);
        assertTrue(password.chars().anyMatch(Character::isUpperCase), "Password should contain at least one uppercase letter");
    }

    @Test
    public void ContainsLowercase() {
        PasswordGenerationService gen = new PasswordGenerationService();
        String password = gen.generatePassword(12);
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
    }   

    @Test
    public void ContainsDigit() {
        PasswordGenerationService gen = new PasswordGenerationService();
        String password = gen.generatePassword(12);
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    public void NoSpaces() {
        PasswordGenerationService gen = new PasswordGenerationService();
        String password = gen.generatePassword(12);
        assertFalse(password.contains(" "));
    }
   
    @Test
    public void ContainsSpecialChar() {
    PasswordGenerationService gen = new PasswordGenerationService();
    String password = gen.generatePassword(12);

    String specialChars = "#!@$%^&*()_+-=[]{}|;:',.<>/?";
    boolean containsSpecialChar = password.chars()
            .anyMatch(ch -> specialChars.indexOf(ch) >= 0);
    assertTrue(containsSpecialChar, "Password should contain at least one special character");
    }

    @Test
    public void MinimumLengthPassword() {
        PasswordGenerationService gen = new PasswordGenerationService();
        String password = gen.generatePassword(8);
        assertEquals(8, password.length());
    }

    @Test
    public void MaximumLengthPassword() {
        PasswordGenerationService gen = new PasswordGenerationService();
        String password = gen.generatePassword(32);
        assertEquals(32, password.length());
    }
}