package com.cpp.moviejournal.manager;

import com.cpp.moviejournal.model.User;
import com.cpp.moviejournal.util.DatabaseConnection;
import com.cpp.moviejournal.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages user-related database operations including registration, authentication,
 * and user profile management.
 */
public class UserManager {
  private static final String CREATE_USERS_TABLE_SQL =
      """
      CREATE TABLE IF NOT EXISTS users (
          id INT AUTO_INCREMENT PRIMARY KEY,
          username VARCHAR(50) NOT NULL UNIQUE,
          email VARCHAR(100) NOT NULL UNIQUE,
          password VARCHAR(255) NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          last_login TIMESTAMP NULL,
          is_active BOOLEAN DEFAULT TRUE,
          INDEX idx_username (username),
          INDEX idx_email (email)
      )
      """;

  private static final String INSERT_USER_SQL =
      "INSERT INTO users (username, email, password, created_at, is_active) VALUES (?, ?, ?, ?, ?)";

  private static final String SELECT_USER_BY_USERNAME_SQL =
      "SELECT * FROM users WHERE username = ? AND is_active = TRUE";

  private static final String SELECT_USER_BY_EMAIL_SQL =
      "SELECT * FROM users WHERE email = ? AND is_active = TRUE";

  private static final String CHECK_USERNAME_EXISTS_SQL = "SELECT COUNT(*) FROM users WHERE username = ?";

  private static final String CHECK_EMAIL_EXISTS_SQL = "SELECT COUNT(*) FROM users WHERE email = ?";

  private static final String SELECT_USER_BY_USERNAME_ALL_SQL = "SELECT * FROM users WHERE username = ?";

  private static final String SELECT_USER_BY_EMAIL_ALL_SQL = "SELECT * FROM users WHERE email = ?";

  private static final String UPDATE_PASSWORD_SQL = "UPDATE users SET password = ? WHERE username = ?";

  private static final String DEACTIVATE_USER_SQL = "UPDATE users SET is_active = FALSE WHERE username = ?";

  private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM users ORDER BY created_at DESC";

  private static final String DELETE_ALL_USERS_SQL = "DELETE FROM users";

  private static final String UPDATE_LAST_LOGIN_SQL = "UPDATE users SET last_login = ? WHERE id = ?";

  private static final String UPDATE_USER_PROFILE_SQL = "UPDATE users SET username = ?, email = ? WHERE id = ?";

  private static final String CHECK_USER_UNIQUENESS_SQL =
      "SELECT COUNT(*) FROM users WHERE (username = ? OR email = ?) AND id != ?";

  public UserManager() {
    initializeDatabase();
  }

  private void initializeDatabase() {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(CREATE_USERS_TABLE_SQL)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println("Error initializing users database: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Registers a new user.
   *
   * @param user the user to register
   * @return true if registration successful, false otherwise
   */
  public boolean registerUser(User user) {
    if (user == null || !user.isValidUser()) {
      return false;
    }
    if (userExists(user.getUsername()) || emailExists(user.getEmail())) {
      return false;
    }
    return insertUser(user);
  }

  private boolean insertUser(User user) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt =
            conn.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
      setUserInsertParameters(stmt, user);
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        setGeneratedUserId(stmt, user);
        return true;
      }
    } catch (SQLException e) {
      System.err.println("Error registering user: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  private void setUserInsertParameters(PreparedStatement stmt, User user) throws SQLException {
    stmt.setString(1, user.getUsername());
    stmt.setString(2, user.getEmail());
    stmt.setString(3, user.getPassword());
    stmt.setTimestamp(4, Timestamp.valueOf(user.getCreatedAt()));
    stmt.setBoolean(5, user.isActive());
  }

  private void setGeneratedUserId(PreparedStatement stmt, User user) throws SQLException {
    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
      if (generatedKeys.next()) {
        user.setId(generatedKeys.getInt(1));
      }
    }
  }

  /**
   * Authenticates a user with username and password.
   *
   * @param username the username
   * @param password the plain text password
   * @return User object if authentication successful, null otherwise
   */
  public User loginUser(String username, String password) {
    if (!isValidCredentials(username, password)) {
      return null;
    }
    return authenticateUser(SELECT_USER_BY_USERNAME_SQL, username.trim(), password);
  }

  /**
   * Authenticates a user with email and password.
   *
   * @param email the email
   * @param password the plain text password
   * @return User object if authentication successful, null otherwise
   */
  public User loginUserByEmail(String email, String password) {
    if (!isValidCredentials(email, password)) {
      return null;
    }
    return authenticateUser(SELECT_USER_BY_EMAIL_SQL, email.trim(), password);
  }

  private boolean isValidCredentials(String identifier, String password) {
    return identifier != null
        && password != null
        && !identifier.trim().isEmpty()
        && !password.trim().isEmpty();
  }

  private User authenticateUser(String sql, String identifier, String password) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, identifier);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          User user = createUserFromResultSet(rs);
          if (user.verifyPassword(password)) {
            updateLastLogin(user.getId());
            user.updateLastLogin();
            return user;
          }
        }
      }
    } catch (SQLException e) {
      System.err.println("Error during login: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Checks if a username already exists.
   *
   * @param username the username to check
   * @return true if username exists, false otherwise
   */
  public boolean userExists(String username) {
    if (username == null || username.trim().isEmpty()) {
      return false;
    }
    return checkExists(CHECK_USERNAME_EXISTS_SQL, username.trim());
  }

  /**
   * Checks if an email already exists.
   *
   * @param email the email to check
   * @return true if email exists, false otherwise
   */
  public boolean emailExists(String email) {
    if (email == null || email.trim().isEmpty()) {
      return false;
    }
    return checkExists(CHECK_EMAIL_EXISTS_SQL, email.trim());
  }

  private boolean checkExists(String sql, String value) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, value);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1) > 0;
        }
      }
    } catch (SQLException e) {
      System.err.println("Error checking existence: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Gets user by username.
   *
   * @param username the username
   * @return User object if found, null otherwise
   */
  public User getUserByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return null;
    }
    return getUserBy(SELECT_USER_BY_USERNAME_ALL_SQL, username.trim());
  }

  /**
   * Gets user by email.
   *
   * @param email the email
   * @return User object if found, null otherwise
   */
  public User getUserByEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return null;
    }
    return getUserBy(SELECT_USER_BY_EMAIL_ALL_SQL, email.trim());
  }

  private User getUserBy(String sql, String identifier) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, identifier);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return createUserFromResultSet(rs);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error getting user: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Updates user password.
   *
   * @param username the username
   * @param oldPassword the current plain text password
   * @param newPassword the new plain text password
   * @return true if password updated successfully, false otherwise
   */
  public boolean updatePassword(String username, String oldPassword, String newPassword) {
    if (!isValidCredentials(username, oldPassword)
        || newPassword == null
        || newPassword.trim().isEmpty()) {
      return false;
    }
    User user = loginUser(username, oldPassword);
    if (user == null) {
      return false;
    }
    return updatePasswordInDatabase(username.trim(), newPassword.trim());
  }

  private boolean updatePasswordInDatabase(String username, String newPassword) {
    String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_PASSWORD_SQL)) {
      stmt.setString(1, hashedNewPassword);
      stmt.setString(2, username);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Error updating password: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Deactivates a user account.
   *
   * @param username the username
   * @return true if deactivated successfully, false otherwise
   */
  public boolean deactivateUser(String username) {
    if (username == null || username.trim().isEmpty()) {
      return false;
    }
    return executeUpdate(DEACTIVATE_USER_SQL, username.trim());
  }

  /**
   * Gets all users (for admin purposes).
   *
   * @return list of all users
   */
  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS_SQL);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        users.add(createUserFromResultSet(rs));
      }
    } catch (SQLException e) {
      System.err.println("Error getting all users: " + e.getMessage());
      e.printStackTrace();
    }
    return users;
  }

  /**
   * Clears all users (useful for testing).
   */
  public void clearAllUsers() {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_USERS_SQL)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println("Error clearing users: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Updates user's profile (username and email). Ensures uniqueness across other users.
   *
   * @param user the user containing updated fields and a valid id
   * @return true if update succeeded, false otherwise
   */
  public boolean updateUserProfile(User user) {
    if (user == null || user.getId() <= 0) {
      return false;
    }
    if (!isUserProfileUnique(user)) {
      return false;
    }
    return updateUserProfileInDatabase(user);
  }

  private boolean isUserProfileUnique(User user) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement checkStmt = conn.prepareStatement(CHECK_USER_UNIQUENESS_SQL)) {
      checkStmt.setString(1, user.getUsername());
      checkStmt.setString(2, user.getEmail());
      checkStmt.setInt(3, user.getId());
      try (ResultSet rs = checkStmt.executeQuery()) {
        if (rs.next() && rs.getInt(1) > 0) {
          return false; // conflict
        }
      }
    } catch (SQLException e) {
      System.err.println("Error checking user uniqueness: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private boolean updateUserProfileInDatabase(User user) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_USER_PROFILE_SQL)) {
      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getEmail());
      stmt.setInt(3, user.getId());
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Error updating user profile: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  // Helper methods
  private User createUserFromResultSet(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String username = rs.getString("username");
    String email = rs.getString("email");
    String password = rs.getString("password");
    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
    LocalDateTime lastLogin =
        rs.getTimestamp("last_login") != null
            ? rs.getTimestamp("last_login").toLocalDateTime()
            : null;
    boolean isActive = rs.getBoolean("is_active");
    return new User(id, username, email, password, createdAt, lastLogin, isActive);
  }

  private void updateLastLogin(int userId) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_LAST_LOGIN_SQL)) {
      stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
      stmt.setInt(2, userId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.err.println("Error updating last login: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private boolean executeUpdate(String sql, String parameter) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, parameter);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Error executing update: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }
}
