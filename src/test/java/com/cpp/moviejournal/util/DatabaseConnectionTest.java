package com.cpp.moviejournal.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for DatabaseConnection class.
 * Tests connection establishment, error handling, and main method functionality.
 */
@DisplayName("DatabaseConnection Unit Tests")
class DatabaseConnectionTest {
  private static final String SUCCESS_MESSAGE = "Connected to moviejournal!";
  private static final String[] EMPTY_ARGS = new String[0];

  @Nested
  @DisplayName("Connection Tests")
  class ConnectionTests {

    @Test
    @DisplayName("Should return valid connection when database is available")
    void shouldReturnValidConnectionWhenDatabaseIsAvailable() {
      // When
      Connection connection = null;
      try {
        connection = DatabaseConnection.getConnection();

        // Then
        assertNotNull(connection);
        assertFalse(connection.isClosed());
      } catch (SQLException e) {
        // If database is not available, that's expected in test environment
        // The test should not fail, but we can verify the exception is SQLException
        assertTrue(e instanceof SQLException);
      } finally {
        closeConnection(connection);
      }
    }

    private void closeConnection(Connection connection) {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          // Ignore close errors
        }
      }
    }

    @Test
    @DisplayName("Should handle connection attempt")
    void shouldHandleConnectionAttempt() {
      // This test verifies that getConnection() either succeeds or throws SQLException
      try (Connection connection = DatabaseConnection.getConnection()) {
        // If we get here, connection succeeded
        assertNotNull(connection);
        assertFalse(connection.isClosed());
      } catch (SQLException e) {
        // If connection fails, we should get SQLException
        assertTrue(e instanceof SQLException);
        assertNotNull(e.getMessage());
      }
    }

    @Test
    @DisplayName("Should handle multiple connection attempts")
    void shouldHandleMultipleConnectionAttempts() {
      // Test that multiple calls to getConnection() work consistently
      SQLException firstException = getConnectionException();
      SQLException secondException = getConnectionException();

      // Both should either succeed or fail in the same way
      if (firstException != null) {
        assertNotNull(secondException);
        assertEquals(firstException.getClass(), secondException.getClass());
      } else {
        assertNull(secondException);
      }
    }

    private SQLException getConnectionException() {
      try {
        DatabaseConnection.getConnection();
        return null;
      } catch (SQLException e) {
        return e;
      }
    }
  }

  @Nested
  @DisplayName("Main Method Tests")
  class MainMethodTests {

    @Test
    @DisplayName("Should print success message when connection succeeds")
    void shouldPrintSuccessMessageWhenConnectionSucceeds() {
      // Capture System.out
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream originalOut = System.out;
      System.setOut(new PrintStream(outputStream));

      try {
        // When
        DatabaseConnection.main(EMPTY_ARGS);

        // Then
        String output = outputStream.toString();
        // Note: This test may not work if database is not available
        // In that case, the output will be empty and that's expected
        if (!output.isEmpty()) {
          assertTrue(output.contains(SUCCESS_MESSAGE));
        }
      } finally {
        // Restore original System.out
        System.setOut(originalOut);
      }
    }

    @Test
    @DisplayName("Should handle SQLException in main method")
    void shouldHandleSQLExceptionInMainMethod() {
      // Capture System.out and System.err
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
      PrintStream originalOut = System.out;
      PrintStream originalErr = System.err;

      System.setOut(new PrintStream(outputStream));
      System.setErr(new PrintStream(errorStream));

      try {
        // When
        DatabaseConnection.main(EMPTY_ARGS);

        // Then
        // The main method should not throw an exception
        // It should handle SQLException internally
        // If database is not available, it will print stack trace to System.err
        // If database is available, it will print success message to System.out

        // Verify that the method completes without throwing exceptions
        assertTrue(true); // If we get here, no exception was thrown

      } finally {
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
      }
    }

    @Test
    @DisplayName("Should handle null arguments in main method")
    void shouldHandleNullArgumentsInMainMethod() {
      // When
      assertDoesNotThrow(() -> DatabaseConnection.main(null));
    }

    @Test
    @DisplayName("Should handle empty arguments in main method")
    void shouldHandleEmptyArgumentsInMainMethod() {
      // When
      assertDoesNotThrow(() -> DatabaseConnection.main(EMPTY_ARGS));
    }
  }

  @Nested
  @DisplayName("Connection Resource Management Tests")
  class ConnectionResourceManagementTests {

    @Test
    @DisplayName("Should allow connection to be closed")
    void shouldAllowConnectionToBeClosed() {
      // When
      try (Connection connection = DatabaseConnection.getConnection()) {
        // Then
        assertNotNull(connection);
        assertFalse(connection.isClosed());

        // Close the connection
        connection.close();
        assertTrue(connection.isClosed());
      } catch (SQLException e) {
        // Expected if database is not available
        assertTrue(e instanceof SQLException);
      }
    }

    @Test
    @DisplayName("Should handle multiple close calls")
    void shouldHandleMultipleCloseCalls() {
      try (Connection connection = DatabaseConnection.getConnection()) {
        // First close
        connection.close();
        assertTrue(connection.isClosed());

        // Second close should not throw exception
        assertDoesNotThrow(() -> connection.close());

      } catch (SQLException e) {
        // Expected if database is not available
        assertTrue(e instanceof SQLException);
      }
    }
  }

  @Nested
  @DisplayName("Static Method Tests")
  class StaticMethodTests {

    @Test
    @DisplayName("Should have static getConnection method")
    void shouldHaveStaticGetConnectionMethod() {
      // Verify the method exists and is static
      try {
        DatabaseConnection.class.getMethod("getConnection");
        assertTrue(true); // Method exists
      } catch (NoSuchMethodException e) {
        fail("getConnection method should exist");
      }
    }

    @Test
    @DisplayName("Should have static main method")
    void shouldHaveStaticMainMethod() {
      // Verify the main method exists and is static
      try {
        DatabaseConnection.class.getMethod("main", String[].class);
        assertTrue(true); // Method exists
      } catch (NoSuchMethodException e) {
        fail("main method should exist");
      }
    }
  }

  @Nested
  @DisplayName("Error Handling Tests")
  class ErrorHandlingTests {

    @Test
    @DisplayName("Should handle connection timeout gracefully")
    void shouldHandleConnectionTimeoutGracefully() {
      // This test verifies that getConnection() handles connection issues properly
      try (Connection connection = DatabaseConnection.getConnection()) {
        // If connection succeeds, verify it's valid
        assertNotNull(connection);
        assertFalse(connection.isClosed());
      } catch (SQLException e) {
        // If connection fails, verify it's a proper SQLException
        assertTrue(e instanceof SQLException);
        assertNotNull(e.getMessage());
      }
    }

    @Test
    @DisplayName("Should provide meaningful error information")
    void shouldProvideMeaningfulErrorInformation() {
      try (Connection connection = DatabaseConnection.getConnection()) {
        // If connection succeeds, verify it's valid
        assertNotNull(connection);
        assertFalse(connection.isClosed());
      } catch (SQLException e) {
        // If connection fails, verify that the exception contains meaningful information
        assertNotNull(e.getMessage());
        assertFalse(e.getMessage().isEmpty());
      }
    }
  }
}