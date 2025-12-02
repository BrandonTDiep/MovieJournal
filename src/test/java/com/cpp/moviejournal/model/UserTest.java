package com.cpp.moviejournal.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cpp.moviejournal.util.PasswordUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive unit tests for User class.
 * Tests all methods with valid and invalid inputs, edge cases, and exception handling.
 */
@DisplayName("User Unit Tests")
class UserTest {
  private static final String VALID_USERNAME = "testuser";
  private static final String VALID_EMAIL = "test@example.com";
  private static final String VALID_PASSWORD = "password123";
  private static final String NEW_USERNAME = "newuser";
  private static final String NEW_EMAIL = "newuser@example.com";
  private static final String NEW_PASSWORD = "newpass123";
  private static final String FULL_USERNAME = "fulluser";
  private static final String FULL_EMAIL = "fulluser@example.com";
  private static final String TEST_PASSWORD = "testpassword123";
  private static final String EMPTY_STRING = "";
  private static final String WHITESPACE_STRING = "   ";
  private static final int TEST_ID = 1;
  private static final int NEW_ID = 42;
  private static final int LONG_STRING_LENGTH = 1000;
  private static final int MAX_USERNAME_LENGTH = 50;
  private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
  private static final String UNICODE_STRING = "用户登录 🎬 电影";
  private static final String WHITESPACE_STRING_FULL = "   \t\n   ";
  private static final String EMAIL_DOMAIN = "@example.com";

  private User validUser;

  @BeforeEach
  void setUp() {
    validUser = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Should create User with default constructor")
    void shouldCreateUserWithDefaultConstructor() {
      // When
      User user = new User();

      // Then
      assertNotNull(user);
      assertTrue(user.isActive());
      assertNotNull(user.getCreatedAt());
      assertNull(user.getUsername());
      assertNull(user.getEmail());
      assertNull(user.getPassword());
    }

    @Test
    @DisplayName("Should create User with username, email, and password")
    void shouldCreateUserWithUsernameEmailAndPassword() {
      // When
      User user = new User(NEW_USERNAME, NEW_EMAIL, NEW_PASSWORD);

      // Then
      assertNotNull(user);
      assertEquals(NEW_USERNAME, user.getUsername());
      assertEquals(NEW_EMAIL, user.getEmail());
      // Password should be hashed, not plain text
      assertFalse(NEW_PASSWORD.equals(user.getPassword()));
      assertTrue(user.isPasswordHashed());
      assertTrue(user.verifyPassword(NEW_PASSWORD)); // Should verify correctly
      assertTrue(user.isActive());
      assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("Should create User with all fields")
    void shouldCreateUserWithAllFields() {
      // Given
      LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
      LocalDateTime lastLogin = LocalDateTime.now().minusHours(2);
      // Create a real BCrypt hash for testing
      String hashedPassword = PasswordUtil.hashPassword(TEST_PASSWORD);

      // When
      User user =
          new User(TEST_ID, FULL_USERNAME, FULL_EMAIL, hashedPassword, createdAt, lastLogin, true);

      // Then
      assertEquals(TEST_ID, user.getId());
      assertEquals(FULL_USERNAME, user.getUsername());
      assertEquals(FULL_EMAIL, user.getEmail());
      assertEquals(hashedPassword, user.getPassword()); // Should store the hashed password as-is
      assertTrue(user.isPasswordHashed());
      assertEquals(createdAt, user.getCreatedAt());
      assertEquals(lastLogin, user.getLastLogin());
      assertTrue(user.isActive());
    }
  }

  @Nested
  @DisplayName("Getter and Setter Tests")
  class GetterSetterTests {

    @Test
    @DisplayName("Should get and set id correctly")
    void shouldGetAndSetIdCorrectly() {
      // Given
      int newId = NEW_ID;

      // When
      validUser.setId(newId);

      // Then
      assertEquals(newId, validUser.getId());
    }

        @Test
        @DisplayName("Should get and set username correctly")
        void shouldGetAndSetUsernameCorrectly() {
            // Given
            String newUsername = "newusername";
            
            // When
            validUser.setUsername(newUsername);
            
            // Then
            assertEquals(newUsername, validUser.getUsername());
        }

        @Test
        @DisplayName("Should handle null username")
        void shouldHandleNullUsername() {
            // When
            validUser.setUsername(null);
            
            // Then
            assertNull(validUser.getUsername());
        }

        @Test
        @DisplayName("Should get and set email correctly")
        void shouldGetAndSetEmailCorrectly() {
            // Given
            String newEmail = "newemail@example.com";
            
            // When
            validUser.setEmail(newEmail);
            
            // Then
            assertEquals(newEmail, validUser.getEmail());
        }

        @Test
        @DisplayName("Should handle null email")
        void shouldHandleNullEmail() {
            // When
            validUser.setEmail(null);
            
            // Then
            assertNull(validUser.getEmail());
        }

        @Test
        @DisplayName("Should get and set password correctly")
        void shouldGetAndSetPasswordCorrectly() {
            // Given
            String newPassword = "newpassword123";
            
            // When
            validUser.setPassword(newPassword);
            
            // Then
            assertEquals(newPassword, validUser.getPassword());
        }
        
        @Test
        @DisplayName("Should set plain text password and hash it")
        void shouldSetPlainTextPasswordAndHashIt() {
            // Given
            String plainPassword = "plaintext123";
            
            // When
            validUser.setPlainTextPassword(plainPassword);
            
            // Then
            assertNotEquals(plainPassword, validUser.getPassword()); // Should be hashed
            assertTrue(validUser.isPasswordHashed());
            assertTrue(validUser.verifyPassword(plainPassword)); // Should verify correctly
        }

        @Test
        @DisplayName("Should handle null password")
        void shouldHandleNullPassword() {
            // When
            validUser.setPassword(null);
            
            // Then
            assertNull(validUser.getPassword());
        }
        
        @Test
        @DisplayName("Should verify correct password")
        void shouldVerifyCorrectPassword() {
            // Given
            String plainPassword = "testpassword123";
            validUser.setPlainTextPassword(plainPassword);
            
            // When
            boolean isValid = validUser.verifyPassword(plainPassword);
            
            // Then
            assertTrue(isValid);
        }
        
        @Test
        @DisplayName("Should reject incorrect password")
        void shouldRejectIncorrectPassword() {
            // Given
            String plainPassword = "testpassword123";
            validUser.setPlainTextPassword(plainPassword);
            
            // When
            boolean isValid = validUser.verifyPassword("wrongpassword");
            
            // Then
            assertFalse(isValid);
        }
        
        @Test
        @DisplayName("Should handle null password verification")
        void shouldHandleNullPasswordVerification() {
            // Given
            String plainPassword = "testpassword123";
            validUser.setPlainTextPassword(plainPassword);
            
            // When
            boolean isValid = validUser.verifyPassword(null);
            
            // Then
            assertFalse(isValid);
        }
        
    @Test
    @DisplayName("Should throw exception for null plain text password")
    void shouldThrowExceptionForNullPlainTextPassword() {
      // When & Then
      assertThrows(IllegalArgumentException.class, () -> validUser.setPlainTextPassword(null));
    }

    @Test
    @DisplayName("Should throw exception for empty plain text password")
    void shouldThrowExceptionForEmptyPlainTextPassword() {
      // When & Then
      assertThrows(IllegalArgumentException.class, () -> validUser.setPlainTextPassword(EMPTY_STRING));
    }

        @Test
        @DisplayName("Should get and set created at correctly")
        void shouldGetAndSetCreatedAtCorrectly() {
            // Given
            LocalDateTime newCreatedAt = LocalDateTime.now().minusDays(5);
            
            // When
            validUser.setCreatedAt(newCreatedAt);
            
            // Then
            assertEquals(newCreatedAt, validUser.getCreatedAt());
        }

        @Test
        @DisplayName("Should get and set last login correctly")
        void shouldGetAndSetLastLoginCorrectly() {
            // Given
            LocalDateTime newLastLogin = LocalDateTime.now().minusHours(1);
            
            // When
            validUser.setLastLogin(newLastLogin);
            
            // Then
            assertEquals(newLastLogin, validUser.getLastLogin());
        }

        @Test
        @DisplayName("Should handle null last login")
        void shouldHandleNullLastLogin() {
            // When
            validUser.setLastLogin(null);
            
            // Then
            assertNull(validUser.getLastLogin());
        }

        @Test
        @DisplayName("Should get and set active status correctly")
        void shouldGetAndSetActiveStatusCorrectly() {
            // When
            validUser.setActive(false);
            
            // Then
            assertFalse(validUser.isActive());
            
            // When
            validUser.setActive(true);
            
            // Then
            assertTrue(validUser.isActive());
        }
    }

  @Nested
  @DisplayName("Business Logic Tests")
  class BusinessLogicTests {

        @Test
        @DisplayName("Should update last login time")
        void shouldUpdateLastLoginTime() {
            // Given
            LocalDateTime beforeUpdate = LocalDateTime.now().minusMinutes(1);
            validUser.setLastLogin(beforeUpdate);
            
            // When
            validUser.updateLastLogin();
            
            // Then
            assertNotNull(validUser.getLastLogin());
            assertTrue(validUser.getLastLogin().isAfter(beforeUpdate));
        }

        @Test
        @DisplayName("Should deactivate user")
        void shouldDeactivateUser() {
            // Given
            assertTrue(validUser.isActive());
            
            // When
            validUser.deactivate();
            
            // Then
            assertFalse(validUser.isActive());
        }

        @Test
        @DisplayName("Should activate user")
        void shouldActivateUser() {
            // Given
            validUser.deactivate();
            assertFalse(validUser.isActive());
            
            // When
            validUser.activate();
            
            // Then
            assertTrue(validUser.isActive());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

    @Test
    @DisplayName("Should validate correct username")
    void shouldValidateCorrectUsername() {
      // Test various valid usernames
      String[] validUsernames = {"user", "testuser", "user123", "a".repeat(MAX_USERNAME_LENGTH)};

      for (String username : validUsernames) {
        validUser.setUsername(username);
        assertTrue(validUser.isValidUsername(), "Username '" + username + "' should be valid");
      }
    }

    @Test
    @DisplayName("Should reject invalid username")
    void shouldRejectInvalidUsername() {
      // Test various invalid usernames
      String[] invalidUsernames = {null, EMPTY_STRING, WHITESPACE_STRING, "ab", "a".repeat(MAX_USERNAME_LENGTH + 1)};

      for (String username : invalidUsernames) {
        validUser.setUsername(username);
        assertFalse(validUser.isValidUsername(), "Username '" + username + "' should be invalid");
      }
    }

        @Test
        @DisplayName("Should validate correct email")
        void shouldValidateCorrectEmail() {
            // Test various valid emails
            String[] validEmails = {"test@example.com", "user.name@domain.co.uk", "a@b.c"};
            
            for (String email : validEmails) {
                validUser.setEmail(email);
                assertTrue(validUser.isValidEmail(), "Email '" + email + "' should be valid");
            }
        }

        @Test
        @DisplayName("Should reject invalid email")
        void shouldRejectInvalidEmail() {
            // Test various invalid emails
            String[] invalidEmails = {null, "", "  ", "invalid", "@example.com", "test@", "test.example.com"};
            
            for (String email : invalidEmails) {
                validUser.setEmail(email);
                assertFalse(validUser.isValidEmail(), "Email '" + email + "' should be invalid");
            }
        }

        @Test
        @DisplayName("Should validate correct password")
        void shouldValidateCorrectPassword() {
            // Test various valid passwords
            String[] validPasswords = {"123456", "password", "a".repeat(100)};
            
            for (String password : validPasswords) {
                validUser.setPassword(password);
                assertTrue(validUser.isValidPassword(), "Password '" + password + "' should be valid");
            }
        }

        @Test
        @DisplayName("Should reject invalid password")
        void shouldRejectInvalidPassword() {
            // Test various invalid passwords
            String[] invalidPasswords = {null, "", "  ", "12345"};
            
            for (String password : invalidPasswords) {
                validUser.setPassword(password);
                assertFalse(validUser.isValidPassword(), "Password '" + password + "' should be invalid");
            }
        }

        @Test
        @DisplayName("Should validate complete valid user")
        void shouldValidateCompleteValidUser() {
            // Given
            validUser.setUsername("validuser");
            validUser.setEmail("valid@example.com");
            validUser.setPassword("validpass123");
            
            // Then
            assertTrue(validUser.isValidUser());
        }

        @Test
        @DisplayName("Should reject invalid user with invalid username")
        void shouldRejectInvalidUserWithInvalidUsername() {
            // Given
            validUser.setUsername("ab"); // Too short
            validUser.setEmail("valid@example.com");
            validUser.setPassword("validpass123");
            
            // Then
            assertFalse(validUser.isValidUser());
        }

        @Test
        @DisplayName("Should reject invalid user with invalid email")
        void shouldRejectInvalidUserWithInvalidEmail() {
            // Given
            validUser.setUsername("validuser");
            validUser.setEmail("invalid-email"); // Missing @ and domain
            validUser.setPassword("validpass123");
            
            // Then
            assertFalse(validUser.isValidUser());
        }

        @Test
        @DisplayName("Should reject invalid user with invalid password")
        void shouldRejectInvalidUserWithInvalidPassword() {
            // Given
            validUser.setUsername("validuser");
            validUser.setEmail("valid@example.com");
            validUser.setPassword("12345"); // Too short
            
            // Then
            assertFalse(validUser.isValidUser());
        }
    }

    @Nested
    @DisplayName("Equals Tests")
    class EqualsTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Then
            assertEquals(validUser, validUser);
        }

        @Test
        @DisplayName("Should be equal to user with same username and email")
        void shouldBeEqualToUserWithSameUsernameAndEmail() {
            // Given
            User otherUser = new User(VALID_USERNAME, VALID_EMAIL, "differentpassword");
            
            // Then
            assertEquals(validUser, otherUser);
        }

        @Test
        @DisplayName("Should not be equal to user with different username")
        void shouldNotBeEqualToUserWithDifferentUsername() {
            // Given
            User otherUser = new User("differentuser", VALID_EMAIL, VALID_PASSWORD);
            
            // Then
            assertNotEquals(validUser, otherUser);
        }

        @Test
        @DisplayName("Should not be equal to user with different email")
        void shouldNotBeEqualToUserWithDifferentEmail() {
            // Given
            User otherUser = new User(VALID_USERNAME, "different@example.com", VALID_PASSWORD);
            
            // Then
            assertNotEquals(validUser, otherUser);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Then
            assertNotEquals(null, validUser);
        }

        @Test
        @DisplayName("Should handle case sensitivity in username and email")
        void shouldHandleCaseSensitivityInUsernameAndEmail() {
            // Given
            User otherUser = new User(VALID_USERNAME.toUpperCase(), VALID_EMAIL.toUpperCase(), VALID_PASSWORD);
            
            // Then
            assertNotEquals(validUser, otherUser);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return correct string format")
        void shouldReturnCorrectStringFormat() {
            // Given
            validUser.setId(1);
            
            // When
            String result = validUser.toString();
            
            // Then
            assertTrue(result.contains("User{"));
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("username='" + VALID_USERNAME + "'"));
            assertTrue(result.contains("email='" + VALID_EMAIL + "'"));
            assertTrue(result.contains("isActive=true"));
        }

        @Test
        @DisplayName("Should handle null fields in toString")
        void shouldHandleNullFieldsInToString() {
            // Given
            User userWithNulls = new User();
            
            // When
            String result = userWithNulls.toString();
            
            // Then
            assertTrue(result.contains("User{"));
            assertTrue(result.contains("username='null'"));
            assertTrue(result.contains("email='null'"));
        }
    }

    @Nested
    @DisplayName("HashCode Tests")
    class HashCodeTests {

        @Test
        @DisplayName("Should have same hash code for equal objects")
        void shouldHaveSameHashCodeForEqualObjects() {
            // Given
            User user1 = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
            User user2 = new User(VALID_USERNAME, VALID_EMAIL, "differentpassword");
            
            // Then
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should have different hash codes for different objects")
        void shouldHaveDifferentHashCodesForDifferentObjects() {
            // Given
            User user1 = new User(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
            User user2 = new User("differentuser", VALID_EMAIL, VALID_PASSWORD);
            
            // Then
            assertNotEquals(user1.hashCode(), user2.hashCode());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

    @Test
    @DisplayName("Should handle very long strings")
    void shouldHandleVeryLongStrings() {
      // Given
      String longString = "A".repeat(LONG_STRING_LENGTH);

      // When
      validUser.setUsername(longString);
      validUser.setEmail(longString + EMAIL_DOMAIN);
      validUser.setPassword(longString);

      // Then
      assertEquals(longString, validUser.getUsername());
      assertEquals(longString + EMAIL_DOMAIN, validUser.getEmail());
      assertEquals(longString, validUser.getPassword());
    }

    @Test
    @DisplayName("Should handle special characters in strings")
    void shouldHandleSpecialCharactersInStrings() {
      // When
      validUser.setUsername(SPECIAL_CHARS);
      validUser.setEmail(SPECIAL_CHARS + EMAIL_DOMAIN);
      validUser.setPassword(SPECIAL_CHARS);

      // Then
      assertEquals(SPECIAL_CHARS, validUser.getUsername());
      assertEquals(SPECIAL_CHARS + EMAIL_DOMAIN, validUser.getEmail());
      assertEquals(SPECIAL_CHARS, validUser.getPassword());
    }

    @Test
    @DisplayName("Should handle unicode characters")
    void shouldHandleUnicodeCharacters() {
      // When
      validUser.setUsername(UNICODE_STRING);
      validUser.setEmail(UNICODE_STRING + EMAIL_DOMAIN);
      validUser.setPassword(UNICODE_STRING);

      // Then
      assertEquals(UNICODE_STRING, validUser.getUsername());
      assertEquals(UNICODE_STRING + EMAIL_DOMAIN, validUser.getEmail());
      assertEquals(UNICODE_STRING, validUser.getPassword());
    }

    @Test
    @DisplayName("Should handle whitespace-only strings")
    void shouldHandleWhitespaceOnlyStrings() {
      // When
      validUser.setUsername(WHITESPACE_STRING_FULL);
      validUser.setEmail(WHITESPACE_STRING_FULL + EMAIL_DOMAIN);
      validUser.setPassword(WHITESPACE_STRING_FULL);

      // Then
      assertEquals(WHITESPACE_STRING_FULL, validUser.getUsername());
      assertEquals(WHITESPACE_STRING_FULL + EMAIL_DOMAIN, validUser.getEmail());
      assertEquals(WHITESPACE_STRING_FULL, validUser.getPassword());
    }

    @Test
    @DisplayName("Should handle boundary values for validation")
    void shouldHandleBoundaryValuesForValidation() {
      // Test minimum valid username length
      validUser.setUsername("abc"); // Exactly 3 characters
      assertTrue(validUser.isValidUsername());

      validUser.setUsername("ab"); // Exactly 2 characters (invalid)
      assertFalse(validUser.isValidUsername());

      // Test minimum valid password length
      validUser.setPassword("123456"); // Exactly 6 characters
      assertTrue(validUser.isValidPassword());

      validUser.setPassword("12345"); // Exactly 5 characters (invalid)
      assertFalse(validUser.isValidPassword());
    }
  }
}
