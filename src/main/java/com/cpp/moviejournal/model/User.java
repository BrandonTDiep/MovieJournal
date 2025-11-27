package com.cpp.moviejournal.model;

import com.cpp.moviejournal.util.PasswordUtil;
import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private int id;
    private String username;
    private String email;
    private String password; // This will store the hashed password
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive;

    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Constructor for new user registration
    public User(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.setPlainTextPassword(password); // Hash the password
    }

    // Constructor for database retrieval (password should already be hashed)
    public User(int id, String username, String email, String password, 
                LocalDateTime createdAt, LocalDateTime lastLogin, boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password; // Assume already hashed from database
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Set the password directly (for database retrieval - assumes already hashed)
     * @param password The hashed password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Set a plain text password and hash it automatically
     * @param plainTextPassword The plain text password to hash and store
     * @throws IllegalArgumentException if password is null or empty
     */
    public void setPlainTextPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = PasswordUtil.hashPassword(plainTextPassword);
    }
    
    /**
     * Verify a plain text password against the stored hashed password
     * @param plainTextPassword The plain text password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainTextPassword) {
        if (plainTextPassword == null || this.password == null) {
            return false;
        }
        return PasswordUtil.verifyPassword(plainTextPassword, this.password);
    }
    
    /**
     * Check if the stored password is already hashed
     * @return true if the password appears to be hashed, false otherwise
     */
    public boolean isPasswordHashed() {
        return PasswordUtil.isHashedPassword(this.password);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Business logic methods
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    // Validation methods
    public boolean isValidUsername() {
        return username != null && username.trim().length() >= 3 && username.trim().length() <= 50;
    }

    public boolean isValidEmail() {
        return email != null && email.contains("@") && email.contains(".") && 
               !email.startsWith("@") && !email.endsWith("@") &&
               email.indexOf("@") < email.lastIndexOf(".");
    }

    public boolean isValidPassword() {
        return password != null && password.length() >= 6;
    }

    public boolean isValidUser() {
        return isValidUsername() && isValidEmail() && isValidPassword();
    }

    // Override methods
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return Objects.equals(username, user.username) && 
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }

    // Builder Pattern for User
    public static class Builder {
        private int id;
        private String username;
        private String email;
        private String password;
        private boolean passwordIsPlaintext;
        private LocalDateTime createdAt;
        private LocalDateTime lastLogin;
        private boolean isActive;

        public Builder() {
            this.isActive = true;
            this.createdAt = LocalDateTime.now();
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPlainTextPassword(String password) {
            this.password = password;
            this.passwordIsPlaintext = true;
            return this;
        }

        public Builder setHashedPassword(String password) {
            this.password = password;
            this.passwordIsPlaintext = false;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setLastLogin(LocalDateTime lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }

        public Builder setActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setEmail(email);
            
            if (password != null) {
                if (passwordIsPlaintext) {
                    user.setPlainTextPassword(password);
                } else {
                    user.setPassword(password);
                }
            }
            
            user.setCreatedAt(createdAt);
            user.setLastLogin(lastLogin);
            user.setActive(isActive);
            
            return user;
        }
    }

    /**
     * Creates a new Builder instance for constructing User objects
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
