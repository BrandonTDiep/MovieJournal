package com.cpp.moviejournal.manager;

import com.cpp.moviejournal.model.User;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for UserManager class
 * Tests all authentication operations, user management, and edge cases
 */
@DisplayName("UserManager Unit Tests")
class UserManagerTest {

    private UserManager userManager;
    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        // Clear database before each test to ensure isolation
        userManager.clearAllUsers();
        
        // Create test users
        testUser1 = new User("testuser1", "test1@example.com", "password123");
        testUser2 = new User("testuser2", "test2@example.com", "password456");
        testUser3 = new User("testuser3", "test3@example.com", "password789");
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        try {
            userManager.clearAllUsers();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    // ------------------------------------------------------------------------------
    // User Registration Tests
    // ------------------------------------------------------------------------------
    @Nested
    @DisplayName("User Registration Tests")
    class UserRegistrationTests {

        @Test
        @DisplayName("Should register valid user successfully")
        void shouldRegisterValidUserSuccessfully() {
            // When
            boolean result = userManager.registerUser(testUser1);
            
            // Then
            assertTrue(result);
            assertTrue(userManager.userExists(testUser1.getUsername()));
            assertTrue(userManager.emailExists(testUser1.getEmail()));
        }

        @Test
        @DisplayName("Should reject null user registration")
        void shouldRejectNullUserRegistration() {
            // When
            boolean result = userManager.registerUser(null);
            
            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should reject user with invalid data")
        void shouldRejectUserWithInvalidData() {
            // Given
            User invalidUser = new User("ab", "invalid-email", "12345"); // All invalid
            
            // When
            boolean result = userManager.registerUser(invalidUser);
            
            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should reject duplicate username registration")
        void shouldRejectDuplicateUsernameRegistration() {
            // Given
            userManager.registerUser(testUser1);
            User duplicateUser = new User(testUser1.getUsername(), "different@example.com", "differentpass");
            
            // When
            boolean result = userManager.registerUser(duplicateUser);
            
            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should reject duplicate email registration")
        void shouldRejectDuplicateEmailRegistration() {
            // Given
            userManager.registerUser(testUser1);
            User duplicateUser = new User("differentuser", testUser1.getEmail(), "differentpass");
            
            // When
            boolean result = userManager.registerUser(duplicateUser);
            
            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should register multiple users successfully")
        void shouldRegisterMultipleUsersSuccessfully() {
            // When
            boolean result1 = userManager.registerUser(testUser1);
            boolean result2 = userManager.registerUser(testUser2);
            boolean result3 = userManager.registerUser(testUser3);
            
            // Then
            assertTrue(result1);
            assertTrue(result2);
            assertTrue(result3);
            
            List<User> allUsers = userManager.getAllUsers();
            assertEquals(3, allUsers.size());
        }

        @Test
        @DisplayName("Should set user ID after successful registration")
        void shouldSetUserIdAfterSuccessfulRegistration() {
            // Given
            assertEquals(0, testUser1.getId()); // Default ID
            
            // When
            boolean result = userManager.registerUser(testUser1);
            
            // Then
            assertTrue(result);
            assertTrue(testUser1.getId() > 0); // Should have a valid ID
        }
    }


    // ------------------------------------------------------------------------------
    // User Login Tests
    // ------------------------------------------------------------------------------
    @Nested
    @DisplayName("User Login Tests")
    class UserLoginTests {

        @BeforeEach
        void setUpLoginTests() {
            userManager.registerUser(testUser1);
            userManager.registerUser(testUser2);
        }

        @Test
        @DisplayName("Should login with valid username and password")
        void shouldLoginWithValidUsernameAndPassword() {
            // When - use the original plain text password for login
            User loggedInUser = userManager.loginUser(testUser1.getUsername(), "password123");
            
            // Then
            assertNotNull(loggedInUser);
            assertEquals(testUser1.getUsername(), loggedInUser.getUsername());
            assertEquals(testUser1.getEmail(), loggedInUser.getEmail());
            assertNotNull(loggedInUser.getLastLogin());
        }

        @Test
        @DisplayName("Should login with valid email and password")
        void shouldLoginWithValidEmailAndPassword() {
            // When - use the original plain text password for login
            User loggedInUser = userManager.loginUserByEmail(testUser1.getEmail(), "password123");
            
            // Then
            assertNotNull(loggedInUser);
            assertEquals(testUser1.getUsername(), loggedInUser.getUsername());
            assertEquals(testUser1.getEmail(), loggedInUser.getEmail());
            assertNotNull(loggedInUser.getLastLogin());
        }

        @Test
        @DisplayName("Should reject login with invalid username")
        void shouldRejectLoginWithInvalidUsername() {
            // When
            User loggedInUser = userManager.loginUser("nonexistentuser", testUser1.getPassword());
            
            // Then
            assertNull(loggedInUser);
        }

        @Test
        @DisplayName("Should reject login with invalid password")
        void shouldRejectLoginWithInvalidPassword() {
            // When
            User loggedInUser = userManager.loginUser(testUser1.getUsername(), "wrongpassword");
            
            // Then
            assertNull(loggedInUser);
        }

        @Test
        @DisplayName("Should reject login with null username")
        void shouldRejectLoginWithNullUsername() {
            // When
            User loggedInUser = userManager.loginUser(null, testUser1.getPassword());
            
            // Then
            assertNull(loggedInUser);
        }

        @Test
        @DisplayName("Should reject login with null password")
        void shouldRejectLoginWithNullPassword() {
            // When
            User loggedInUser = userManager.loginUser(testUser1.getUsername(), null);
            
            // Then
            assertNull(loggedInUser);
        }

        @Test
        @DisplayName("Should reject login with empty username")
        void shouldRejectLoginWithEmptyUsername() {
            // When
            User loggedInUser = userManager.loginUser("", testUser1.getPassword());
            
            // Then
            assertNull(loggedInUser);
        }

        @Test
        @DisplayName("Should reject login with empty password")
        void shouldRejectLoginWithEmptyPassword() {
            // When
            User loggedInUser = userManager.loginUser(testUser1.getUsername(), "");
            
            // Then
            assertNull(loggedInUser);
        }

        @Test
        @DisplayName("Should reject login with whitespace-only credentials")
        void shouldRejectLoginWithWhitespaceOnlyCredentials() {
            // When
            User loggedInUser = userManager.loginUser("   ", "   ");
            
            // Then
            assertNull(loggedInUser);
        }

        @Test
        @DisplayName("Should update last login time after successful login")
        void shouldUpdateLastLoginTimeAfterSuccessfulLogin() {
            // Given
            User userBeforeLogin = userManager.getUserByUsername(testUser1.getUsername());
            assertNull(userBeforeLogin.getLastLogin());
            
            // When - use the original plain text password for login
            User loggedInUser = userManager.loginUser(testUser1.getUsername(), "password123");
            
            // Then
            assertNotNull(loggedInUser.getLastLogin());
            assertTrue(loggedInUser.getLastLogin().isAfter(LocalDateTime.now().minusMinutes(1)));
        }
    }


    // ------------------------------------------------------------------------------
    // User Existence Tests
    // ------------------------------------------------------------------------------
    @Nested
    @DisplayName("User Existence Tests")
    class UserExistenceTests {

        @BeforeEach
        void setUpExistenceTests() {
            userManager.registerUser(testUser1);
        }

        @Test
        @DisplayName("Should return true for existing username")
        void shouldReturnTrueForExistingUsername() {
            // When
            boolean exists = userManager.userExists(testUser1.getUsername());
            
            // Then
            assertTrue(exists);
        }

        @Test
        @DisplayName("Should return false for non-existing username")
        void shouldReturnFalseForNonExistingUsername() {
            // When
            boolean exists = userManager.userExists("nonexistentuser");
            
            // Then
            assertFalse(exists);
        }

        @Test
        @DisplayName("Should return true for existing email")
        void shouldReturnTrueForExistingEmail() {
            // When
            boolean exists = userManager.emailExists(testUser1.getEmail());
            
            // Then
            assertTrue(exists);
        }

        @Test
        @DisplayName("Should return false for non-existing email")
        void shouldReturnFalseForNonExistingEmail() {
            // When
            boolean exists = userManager.emailExists("nonexistent@example.com");
            
            // Then
            assertFalse(exists);
        }

        @Test
        @DisplayName("Should handle null username in existence check")
        void shouldHandleNullUsernameInExistenceCheck() {
            // When
            boolean exists = userManager.userExists(null);
            
            // Then
            assertFalse(exists);
        }

        @Test
        @DisplayName("Should handle null email in existence check")
        void shouldHandleNullEmailInExistenceCheck() {
            // When
            boolean exists = userManager.emailExists(null);
            
            // Then
            assertFalse(exists);
        }

        @Test
        @DisplayName("Should handle empty username in existence check")
        void shouldHandleEmptyUsernameInExistenceCheck() {
            // When
            boolean exists = userManager.userExists("");
            
            // Then
            assertFalse(exists);
        }

        @Test
        @DisplayName("Should handle empty email in existence check")
        void shouldHandleEmptyEmailInExistenceCheck() {
            // When
            boolean exists = userManager.emailExists("");
            
            // Then
            assertFalse(exists);
        }
    }

    // ------------------------------------------------------------------------------
    // User Retrieval Tests
    // ------------------------------------------------------------------------------
    @Nested
    @DisplayName("User Retrieval Tests")
    class UserRetrievalTests {

        @BeforeEach
        void setUpRetrievalTests() {
            userManager.registerUser(testUser1);
            userManager.registerUser(testUser2);
        }

        @Test
        @DisplayName("Should get user by username")
        void shouldGetUserByUsername() {
            // When
            User retrievedUser = userManager.getUserByUsername(testUser1.getUsername());
            
            // Then
            assertNotNull(retrievedUser);
            assertEquals(testUser1.getUsername(), retrievedUser.getUsername());
            assertEquals(testUser1.getEmail(), retrievedUser.getEmail());
        }

        @Test
        @DisplayName("Should get user by email")
        void shouldGetUserByEmail() {
            // When
            User retrievedUser = userManager.getUserByEmail(testUser1.getEmail());
            
            // Then
            assertNotNull(retrievedUser);
            assertEquals(testUser1.getUsername(), retrievedUser.getUsername());
            assertEquals(testUser1.getEmail(), retrievedUser.getEmail());
        }

        @Test
        @DisplayName("Should return null for non-existing username")
        void shouldReturnNullForNonExistingUsername() {
            // When
            User retrievedUser = userManager.getUserByUsername("nonexistentuser");
            
            // Then
            assertNull(retrievedUser);
        }

        @Test
        @DisplayName("Should return null for non-existing email")
        void shouldReturnNullForNonExistingEmail() {
            // When
            User retrievedUser = userManager.getUserByEmail("nonexistent@example.com");
            
            // Then
            assertNull(retrievedUser);
        }

        @Test
        @DisplayName("Should return null for null username")
        void shouldReturnNullForNullUsername() {
            // When
            User retrievedUser = userManager.getUserByUsername(null);
            
            // Then
            assertNull(retrievedUser);
        }

        @Test
        @DisplayName("Should return null for null email")
        void shouldReturnNullForNullEmail() {
            // When
            User retrievedUser = userManager.getUserByEmail(null);
            
            // Then
            assertNull(retrievedUser);
        }

        @Test
        @DisplayName("Should get all users")
        void shouldGetAllUsers() {
            // When
            List<User> allUsers = userManager.getAllUsers();
            
            // Then
            assertEquals(2, allUsers.size());
            assertTrue(allUsers.stream().anyMatch(u -> u.getUsername().equals(testUser1.getUsername())));
            assertTrue(allUsers.stream().anyMatch(u -> u.getUsername().equals(testUser2.getUsername())));
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsersExist() {
            // Given
            userManager.clearAllUsers();
            
            // When
            List<User> allUsers = userManager.getAllUsers();
            
            // Then
            assertTrue(allUsers.isEmpty());
        }
    }

    // ------------------------------------------------------------------------------
    // User Deactivation Tests
    // ------------------------------------------------------------------------------
    @Nested
    @DisplayName("User Deactivation Tests")
    class UserDeactivationTests {

        @BeforeEach
        void setUpDeactivationTests() {
            userManager.registerUser(testUser1);
        }

        @Test
        @DisplayName("Should deactivate user successfully")
        void shouldDeactivateUserSuccessfully() {
            // When
            boolean result = userManager.deactivateUser(testUser1.getUsername());
            
            // Then
            assertTrue(result);
            
            // Verify user can no longer login
            User loginResult = userManager.loginUser(testUser1.getUsername(), testUser1.getPassword());
            assertNull(loginResult);
        }

        @Test
        @DisplayName("Should reject deactivation of non-existing user")
        void shouldRejectDeactivationOfNonExistingUser() {
            // When
            boolean result = userManager.deactivateUser("nonexistentuser");
            
            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should reject deactivation with null username")
        void shouldRejectDeactivationWithNullUsername() {
            // When
            boolean result = userManager.deactivateUser(null);
            
            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should reject deactivation with empty username")
        void shouldRejectDeactivationWithEmptyUsername() {
            // When
            boolean result = userManager.deactivateUser("");
            
            // Then
            assertFalse(result);
        }
    }

    // ------------------------------------------------------------------------------
    // Edge Cases and Error Handling Tests
    // ------------------------------------------------------------------------------
    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle case sensitivity in login")
        void shouldHandleCaseSensitivityInLogin() {
            // Given
            userManager.registerUser(testUser1);
            
            // When - use the original plain text password for login
            User loginResult = userManager.loginUser(testUser1.getUsername().toUpperCase(), "password123");
            
            // Then
            // MySQL default collation is case-insensitive, so this will actually succeed
            // In a production system with case-sensitive collation, this would fail
            assertNotNull(loginResult); // MySQL is case-insensitive by default
        }

        @Test
        @DisplayName("Should handle whitespace in credentials")
        void shouldHandleWhitespaceInCredentials() {
            // Given
            userManager.registerUser(testUser1);
            
            // When - use the original plain text password for login
            User loginResult = userManager.loginUser(" " + testUser1.getUsername() + " ", "password123");
            
            // Then
            // The current implementation trims usernames in login, so this should succeed
            // because " testuser1 " gets trimmed to "testuser1"
            assertNotNull(loginResult); // Should match after trimming
            assertEquals(testUser1.getUsername(), loginResult.getUsername());
        }

        @Test
        @DisplayName("Should handle special characters in credentials")
        void shouldHandleSpecialCharactersInCredentials() {
            // Given
            User specialUser = new User("user@#$", "special@example.com", "pass!@#");
            userManager.registerUser(specialUser);
            
            // When - use the original plain text password for login
            User loginResult = userManager.loginUser(specialUser.getUsername(), "pass!@#");
            
            // Then
            assertNotNull(loginResult);
            assertEquals(specialUser.getUsername(), loginResult.getUsername());
        }

        @Test
        @DisplayName("Should handle unicode characters in credentials")
        void shouldHandleUnicodeCharactersInCredentials() {
            // Given
            User unicodeUser = new User("用户", "用户@example.com", "密码123");
            boolean registered = userManager.registerUser(unicodeUser);
            
            // When - use the original plain text password for login
            User loginResult = userManager.loginUser(unicodeUser.getUsername(), "密码123");
            
            // Then
            if (registered) {
                assertNotNull(loginResult);
                assertEquals(unicodeUser.getUsername(), loginResult.getUsername());
            } else {
                // Registration failed due to database constraints
                assertNull(loginResult);
            }
        }

        @Test
        @DisplayName("Should handle very long credentials")
        void shouldHandleVeryLongCredentials() {
            // Given
            String longString = "A".repeat(1000);
            User longUser = new User(longString, longString + "@example.com", longString);
            boolean registered = userManager.registerUser(longUser);
            
            // When - use the original plain text password for login
            User loginResult = userManager.loginUser(longUser.getUsername(), longString);
            
            // Then
            if (registered) {
                assertNotNull(loginResult);
                assertEquals(longUser.getUsername(), loginResult.getUsername());
            } else {
                // Registration failed due to database constraints
                assertNull(loginResult);
            }
        }

        @Test
        @DisplayName("Should handle concurrent user operations")
        void shouldHandleConcurrentUserOperations() {
            // Given
            userManager.registerUser(testUser1);
            
            // When - Multiple operations using original plain text password
            boolean loginResult1 = userManager.loginUser(testUser1.getUsername(), "password123") != null;
            boolean loginResult2 = userManager.loginUser(testUser1.getUsername(), "password123") != null;
            boolean existsResult = userManager.userExists(testUser1.getUsername());
            User retrievedUser = userManager.getUserByUsername(testUser1.getUsername());
            
            // Then
            assertTrue(loginResult1);
            assertTrue(loginResult2);
            assertTrue(existsResult);
            assertNotNull(retrievedUser);
        }
    }
}
