package com.cpp.moviejournal.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Pattern: Ensures only one instance of database connection manager exists
 */
public class DatabaseConnection {
    
    // Singleton instance
    private static volatile DatabaseConnection instance;
    
    // Use java-dotenv to load .env (if present). We still prefer real environment variables
    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

    // Try environment variable, system property, then .env via Dotenv, then fallback default
    private static String getEnv(String key, String defaultValue) {
        String v = System.getenv(key);
        if (v != null && !v.isEmpty()) return v;
        v = System.getProperty(key);
        if (v != null && !v.isEmpty()) return v;
        v = DOTENV.get(key);
        if (v != null && !v.isEmpty()) return v;
        return defaultValue;
    }

    // Keys: DB_URL, DB_USER, DB_PASSWORD
    private final String URL = getEnv("DB_URL", "");
    private final String USER = getEnv("DB_USER", "");
    private final String PASSWORD = getEnv("DB_PASSWORD", "");

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
        // Private constructor for singleton
    }

    /**
     * Singleton Pattern: Returns the single instance of DatabaseConnection
     * Uses double-checked locking for thread safety
     * @return The singleton instance
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
     * Gets a database connection (instance method)
     * @return A new Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnectionInstance() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Static convenience method for backward compatibility
     * @return A new Connection object
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

