package com.cpp.moviejournal.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtil class
 * Tests password hashing, verification, and edge cases
 */
@DisplayName("PasswordUtil Unit Tests")
class PasswordUtilTest {

    private static final String TEST_PASSWORD = "testPassword123";
    private static final String TEST_PASSWORD_2 = "anotherPassword456";

    @Nested
    @DisplayName("Password Hashing Tests")
    class PasswordHashingTests {

        @Test
        @DisplayName("Should hash password successfully")
        void shouldHashPasswordSuccessfully() {
            // When
            String hashedPassword = PasswordUtil.hashPassword(TEST_PASSWORD);

            // Then
            assertNotNull(hashedPassword);
            assertNotEquals(TEST_PASSWORD, hashedPassword);
            assertTrue(PasswordUtil.isHashedPassword(hashedPassword));
        }

        @Test
        @DisplayName("Should generate different hashes for same password")
        void shouldGenerateDifferentHashesForSamePassword() {
            // When
            String hash1 = PasswordUtil.hashPassword(TEST_PASSWORD);
            String hash2 = PasswordUtil.hashPassword(TEST_PASSWORD);

            // Then
            assertNotEquals(hash1, hash2); // Different salts should produce different hashes
            assertTrue(PasswordUtil.verifyPassword(TEST_PASSWORD, hash1));
            assertTrue(PasswordUtil.verifyPassword(TEST_PASSWORD, hash2));
        }

        @Test
        @DisplayName("Should throw exception for null password")
        void shouldThrowExceptionForNullPassword() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PasswordUtil.hashPassword(null);
            });
        }

        @Test
        @DisplayName("Should throw exception for empty password")
        void shouldThrowExceptionForEmptyPassword() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PasswordUtil.hashPassword("");
            });
        }

        @Test
        @DisplayName("Should throw exception for whitespace-only password")
        void shouldThrowExceptionForWhitespaceOnlyPassword() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PasswordUtil.hashPassword("   ");
            });
        }
    }

    @Nested
    @DisplayName("Password Verification Tests")
    class PasswordVerificationTests {

        @Test
        @DisplayName("Should verify correct password")
        void shouldVerifyCorrectPassword() {
            // Given
            String hashedPassword = PasswordUtil.hashPassword(TEST_PASSWORD);

            // When
            boolean isValid = PasswordUtil.verifyPassword(TEST_PASSWORD, hashedPassword);

            // Then
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Should reject incorrect password")
        void shouldRejectIncorrectPassword() {
            // Given
            String hashedPassword = PasswordUtil.hashPassword(TEST_PASSWORD);

            // When
            boolean isValid = PasswordUtil.verifyPassword(TEST_PASSWORD_2, hashedPassword);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should handle null plain password")
        void shouldHandleNullPlainPassword() {
            // Given
            String hashedPassword = PasswordUtil.hashPassword(TEST_PASSWORD);

            // When
            boolean isValid = PasswordUtil.verifyPassword(null, hashedPassword);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should handle null hashed password")
        void shouldHandleNullHashedPassword() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                PasswordUtil.verifyPassword(TEST_PASSWORD, null);
            });
        }

        @Test
        @DisplayName("Should handle malformed hash")
        void shouldHandleMalformedHash() {
            // Given
            String malformedHash = "not_a_valid_hash";

            // When
            boolean isValid = PasswordUtil.verifyPassword(TEST_PASSWORD, malformedHash);

            // Then
            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("Hash Detection Tests")
    class HashDetectionTests {

        @Test
        @DisplayName("Should detect valid BCrypt hash")
        void shouldDetectValidBCryptHash() {
            // Given
            String hashedPassword = PasswordUtil.hashPassword(TEST_PASSWORD);

            // When
            boolean isHashed = PasswordUtil.isHashedPassword(hashedPassword);

            // Then
            assertTrue(isHashed);
        }

        @Test
        @DisplayName("Should reject null as hash")
        void shouldRejectNullAsHash() {
            // When
            boolean isHashed = PasswordUtil.isHashedPassword(null);

            // Then
            assertFalse(isHashed);
        }

        @Test
        @DisplayName("Should reject empty string as hash")
        void shouldRejectEmptyStringAsHash() {
            // When
            boolean isHashed = PasswordUtil.isHashedPassword("");

            // Then
            assertFalse(isHashed);
        }

        @Test
        @DisplayName("Should reject wrong length string as hash")
        void shouldRejectWrongLengthStringAsHash() {
            // When
            boolean isHashed = PasswordUtil.isHashedPassword("short");

            // Then
            assertFalse(isHashed);
        }

        @Test
        @DisplayName("Should reject string without BCrypt prefix as hash")
        void shouldRejectStringWithoutBCryptPrefixAsHash() {
            // Given
            String fakeHash = "A".repeat(60); // Right length but wrong prefix

            // When
            boolean isHashed = PasswordUtil.isHashedPassword(fakeHash);

            // Then
            assertFalse(isHashed);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Characters")
    class EdgeCasesAndSpecialCharactersTests {

        @Test
        @DisplayName("Should handle special characters in password")
        void shouldHandleSpecialCharactersInPassword() {
            // Given
            String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";

            // When
            String hashedPassword = PasswordUtil.hashPassword(specialPassword);
            boolean isValid = PasswordUtil.verifyPassword(specialPassword, hashedPassword);

            // Then
            assertNotNull(hashedPassword);
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Should handle unicode characters in password")
        void shouldHandleUnicodeCharactersInPassword() {
            // Given
            String unicodePassword = "密码123 🎬 电影";

            // When
            String hashedPassword = PasswordUtil.hashPassword(unicodePassword);
            boolean isValid = PasswordUtil.verifyPassword(unicodePassword, hashedPassword);

            // Then
            assertNotNull(hashedPassword);
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Should handle very long password")
        void shouldHandleVeryLongPassword() {
            // Given
            String longPassword = "A".repeat(1000);

            // When
            String hashedPassword = PasswordUtil.hashPassword(longPassword);
            boolean isValid = PasswordUtil.verifyPassword(longPassword, hashedPassword);

            // Then
            assertNotNull(hashedPassword);
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Should handle very short password")
        void shouldHandleVeryShortPassword() {
            // Given
            String shortPassword = "a";

            // When
            String hashedPassword = PasswordUtil.hashPassword(shortPassword);
            boolean isValid = PasswordUtil.verifyPassword(shortPassword, hashedPassword);

            // Then
            assertNotNull(hashedPassword);
            assertTrue(isValid);
        }
    }

    @Nested
    @DisplayName("Work Factor Tests")
    class WorkFactorTests {

        @Test
        @DisplayName("Should return correct work factor")
        void shouldReturnCorrectWorkFactor() {
            // When
            int workFactor = PasswordUtil.getWorkFactor();

            // Then
            assertEquals(12, workFactor);
        }
    }
}
