package com.cpp.moviejournal.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

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
    private static final String URL = getEnv("DB_URL", "");
    private static final String USER = getEnv("DB_USER", "");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
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

