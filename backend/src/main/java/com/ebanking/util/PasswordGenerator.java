package com.ebanking.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Utility class for generating secure passwords
 */
@Component
public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*";
    
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
    private static final int DEFAULT_PASSWORD_LENGTH = 12;
    
    private final SecureRandom random = new SecureRandom();

    /**
     * Generate a secure password with default length (12 characters)
     * 
     * @return generated password
     */
    public String generatePassword() {
        return generatePassword(DEFAULT_PASSWORD_LENGTH);
    }

    /**
     * Generate a secure password with specified length
     * 
     * @param length the desired password length (minimum 8)
     * @return generated password
     */
    public String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder(length);
        
        // Ensure at least one character from each category
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        
        // Fill the rest with random characters from all categories
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        
        // Shuffle the password to avoid predictable patterns
        return shuffleString(password.toString());
    }

    /**
     * Shuffle the characters in a string
     * 
     * @param input the string to shuffle
     * @return shuffled string
     */
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        
        return new String(characters);
    }
}
