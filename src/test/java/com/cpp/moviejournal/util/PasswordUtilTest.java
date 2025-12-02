package com.cpp.moviejournal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PasswordUtil class.
 * Tests password hashing, verification, and edge cases.
 */
@DisplayName("PasswordUtil Unit Tests")
class PasswordUtilTest {
  private static final String TEST_PASSWORD = "testPassword123";
  private static final String TEST_PASSWORD_2 = "anotherPassword456";
  private static final String EMPTY_STRING = "";
  private static final String WHITESPACE_STRING = "   ";
  private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
  private static final String UNICODE_PASSWORD = "密码123 🎬 电影";
  private static final int LONG_PASSWORD_LENGTH = 1000;
  private static final int BCRYPT_HASH_LENGTH = 60;
  private static final int EXPECTED_WORK_FACTOR = 12;
  private static final String SHORT_PASSWORD = "a";

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
      assertFalse(TEST_PASSWORD.equals(hashedPassword));
      assertTrue(PasswordUtil.isHashedPassword(hashedPassword));
    }

    @Test
    @DisplayName("Should generate different hashes for same password")
    void shouldGenerateDifferentHashesForSamePassword() {
      // When
      String hash1 = PasswordUtil.hashPassword(TEST_PASSWORD);
      String hash2 = PasswordUtil.hashPassword(TEST_PASSWORD);

      // Then
      assertFalse(hash1.equals(hash2)); // Different salts should produce different hashes
      assertTrue(PasswordUtil.verifyPassword(TEST_PASSWORD, hash1));
      assertTrue(PasswordUtil.verifyPassword(TEST_PASSWORD, hash2));
    }

    @Test
    @DisplayName("Should throw exception for null password")
    void shouldThrowExceptionForNullPassword() {
      // When & Then
      assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
    }

    @Test
    @DisplayName("Should throw exception for empty password")
    void shouldThrowExceptionForEmptyPassword() {
      // When & Then
      assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(EMPTY_STRING));
    }

    @Test
    @DisplayName("Should throw exception for whitespace-only password")
    void shouldThrowExceptionForWhitespaceOnlyPassword() {
      // When & Then
      assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(WHITESPACE_STRING));
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
      assertThrows(IllegalArgumentException.class, () -> PasswordUtil.verifyPassword(TEST_PASSWORD, null));
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
      boolean isHashed = PasswordUtil.isHashedPassword(EMPTY_STRING);

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
      String fakeHash = "A".repeat(BCRYPT_HASH_LENGTH); // Right length but wrong prefix

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
      String specialPassword = SPECIAL_CHARS;

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
      String unicodePassword = UNICODE_PASSWORD;

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
      String longPassword = "A".repeat(LONG_PASSWORD_LENGTH);

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
      String shortPassword = SHORT_PASSWORD;

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
      assertEquals(EXPECTED_WORK_FACTOR, workFactor);
    }
  }
}
