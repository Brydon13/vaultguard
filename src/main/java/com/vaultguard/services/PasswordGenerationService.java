package com.vaultguard.services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerationService {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:',.<>/?";
    private static final SecureRandom random = new SecureRandom();

    public String generatePassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4");
        }

        List<Character> passwordList = new ArrayList<>();

        passwordList.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        passwordList.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        passwordList.add(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        passwordList.add(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        String allChars = UPPERCASE + LOWERCASE + NUMBERS + SPECIAL_CHARS;

        for (int i = 4; i < length; i++) {
            passwordList.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        Collections.shuffle(passwordList, random);

        StringBuilder password = new StringBuilder();
        for (char c : passwordList) {
            password.append(c);
        }
        return password.toString();
    }
}
