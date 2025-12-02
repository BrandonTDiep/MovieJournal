package com.cpp.moviejournal.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Pattern: Ensures only one instance of database connection manager exists.
 * Provides thread-safe access to database connections with configuration loaded from
 * environment variables, system properties, or .env file.
 */
public class DatabaseConnection {
  private static final String ENV_DB_URL = "DB_URL";
  private static final String ENV_DB_USER = "DB_USER";
  private static final String ENV_DB_PASSWORD = "DB_PASSWORD";
  private static final String EMPTY_STRING = "";

  // Singleton instance
  private static volatile DatabaseConnection instance;

  // Use java-dotenv to load .env (if present). We still prefer real environment variables
  private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

  // Keys: DB_URL, DB_USER, DB_PASSWORD
  private final String url;
  private final String user;
  private final String password;

  // Private constructor to prevent instantiation
  private DatabaseConnection() {
    this.url = getEnv(ENV_DB_URL, EMPTY_STRING);
    this.user = getEnv(ENV_DB_USER, EMPTY_STRING);
    this.password = getEnv(ENV_DB_PASSWORD, EMPTY_STRING);
  }

  /**
   * Gets environment variable value, trying environment variables, system properties,
   * then .env file, then fallback default.
   *
   * @param key the environment variable key
   * @param defaultValue the default value if not found
   * @return the environment variable value or default
   */
  private static String getEnv(String key, String defaultValue) {
    String value = System.getenv(key);
    if (value != null && !value.isEmpty()) {
      return value;
    }
    value = System.getProperty(key);
    if (value != null && !value.isEmpty()) {
      return value;
    }
    value = DOTENV.get(key);
    if (value != null && !value.isEmpty()) {
      return value;
    }
    return defaultValue;
  }

  /**
   * Singleton Pattern: Returns the single instance of DatabaseConnection.
   * Uses double-checked locking for thread safety.
   *
   * @return the singleton instance
   */
  public static DatabaseConnection getInstance() {
    if (instance == null) {
      synchronized (DatabaseConnection.class) {
        if (instance == null) {
          instance = new DatabaseConnection();
        }
      }
    }
    return instance;
  }

  /**
   * Gets a database connection (instance method).
   *
   * @return a new Connection object
   * @throws SQLException if connection fails
   */
  public Connection getConnectionInstance() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  /**
   * Static convenience method for backward compatibility.
   *
   * @return a new Connection object
   * @throws SQLException if connection fails
   */
  public static Connection getConnection() throws SQLException {
    return getInstance().getConnectionInstance();
  }

  public static void main(String[] args) {
    try (Connection conn = getConnection()) {
      if (conn != null) {
        System.out.println("Connected to moviejournal!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}

