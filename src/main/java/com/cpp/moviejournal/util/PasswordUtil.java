package com.cpp.moviejournal.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for secure password hashing and verification using BCrypt
 * BCrypt is a strong, adaptive hashing function that automatically handles salting
 * and is resistant to rainbow table attacks and timing attacks.
 */
public class PasswordUtil {
    
    // BCrypt work factor (cost factor) - higher values are more secure but slower
    // 12 is a good balance between security and performance for most applications
    private static final int BCRYPT_ROUNDS = 12;
    
    /**
     * Hash a plain text password using BCrypt
     * @param plainPassword The plain text password to hash
     * @return The hashed password string
     * @throws IllegalArgumentException if plainPassword is null or empty
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // BCrypt automatically generates a random salt for each password
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Verify a plain text password against a hashed password
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to verify against
     * @return true if the password matches, false otherwise
     * @throws IllegalArgumentException if hashedPassword is null
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null) {
            throw new IllegalArgumentException("Hashed password cannot be null");
        }
        if (plainPassword == null) {
            return false; // Null plain password should return false, not throw exception
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            // If there's any error in verification (e.g., malformed hash), return false
            return false;
        }
    }
    
    /**
     * Check if a password string appears to be a BCrypt hash
     * BCrypt hashes typically start with $2a$, $2b$, or $2y$ and are 60 characters long
     * @param password The password string to check
     * @return true if it appears to be a BCrypt hash, false otherwise
     */
    public static boolean isHashedPassword(String password) {
        if (password == null || password.length() != 60) {
            return false;
        }
        
        return password.startsWith("$2a$") || 
               password.startsWith("$2b$") || 
               password.startsWith("$2y$");
    }
    
    /**
     * Get the BCrypt work factor (cost factor) used for hashing
     * @return The work factor
     */
    public static int getWorkFactor() {
        return BCRYPT_ROUNDS;
    }
}
