package com.ebanking.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordGenerator
 */
class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator();
    }

    @Test
    void testGeneratePasswordWithDefaultLength() {
        // When
        String password = passwordGenerator.generatePassword();

        // Then
        assertNotNull(password);
        assertEquals(12, password.length());
        assertTrue(containsUppercase(password));
        assertTrue(containsLowercase(password));
        assertTrue(containsDigit(password));
        assertTrue(containsSpecialChar(password));
    }

    @Test
    void testGeneratePasswordWithCustomLength() {
        // Given
        int customLength = 16;

        // When
        String password = passwordGenerator.generatePassword(customLength);

        // Then
        assertNotNull(password);
        assertEquals(customLength, password.length());
        assertTrue(containsUppercase(password));
        assertTrue(containsLowercase(password));
        assertTrue(containsDigit(password));
        assertTrue(containsSpecialChar(password));
    }

    @Test
    void testGeneratePasswordWithMinimumLength() {
        // Given
        int minLength = 8;

        // When
        String password = passwordGenerator.generatePassword(minLength);

        // Then
        assertNotNull(password);
        assertEquals(minLength, password.length());
    }

    @Test
    void testGeneratePasswordThrowsExceptionForShortLength() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordGenerator.generatePassword(7);
        });
    }

    @Test
    void testGeneratedPasswordsAreDifferent() {
        // When
        String password1 = passwordGenerator.generatePassword();
        String password2 = passwordGenerator.generatePassword();

        // Then
        assertNotEquals(password1, password2);
    }

    private boolean containsUppercase(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

    private boolean containsLowercase(String password) {
        return password.chars().anyMatch(Character::isLowerCase);
    }

    private boolean containsDigit(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    private boolean containsSpecialChar(String password) {
        String specialChars = "!@#$%^&*";
        return password.chars().anyMatch(ch -> specialChars.indexOf(ch) >= 0);
    }
}
