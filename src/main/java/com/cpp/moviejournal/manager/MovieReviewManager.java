package com.cpp.moviejournal.manager;

import com.cpp.moviejournal.model.MovieReview;
import com.cpp.moviejournal.strategy.SortStrategy;
import com.cpp.moviejournal.strategy.SortStrategyFactory;
import com.cpp.moviejournal.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages movie review-related database operations including CRUD operations,
 * searching, sorting, and observer notifications.
 */
public class MovieReviewManager {
  private static final String DEFAULT_TITLE = "Untitled";
  private static final String DEFAULT_DIRECTOR = "Unknown";
  private static final String EMPTY_STRING = "";
  private static final String TABLE_MOVIE_REVIEWS = "movie_reviews";
  private static final String COLUMN_TICKET_IMAGE_PATH = "ticket_image_path";
  private static final String COLUMN_IS_FAVORITE = "is_favorite";

  private static final String CREATE_MOVIE_REVIEWS_TABLE_SQL =
      """
      CREATE TABLE IF NOT EXISTS movie_reviews (
          id INT AUTO_INCREMENT PRIMARY KEY,
          user_id INT NOT NULL,
          title VARCHAR(255) NOT NULL,
          director VARCHAR(255) NOT NULL,
          genre VARCHAR(100) NOT NULL,
          rating DECIMAL(2,1) NOT NULL CHECK (rating >= 0 AND rating <= 5),
          review TEXT,
          date_watched DATE NOT NULL,
          ticket_image_path VARCHAR(500),
          is_favorite BOOLEAN DEFAULT FALSE,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
          UNIQUE KEY unique_user_movie_director (user_id, title, director)
      )
      """;

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

  private static final String CHECK_USER_EXISTS_SQL = "SELECT COUNT(*) FROM users WHERE id = ?";
  private static final String INSERT_TEST_USER_SQL =
      "INSERT INTO users (id, username, email, password, created_at, is_active) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, TRUE)";

  private static final String INSERT_REVIEW_SQL =
      "INSERT INTO movie_reviews (user_id, title, director, genre, rating, review, date_watched, ticket_image_path, is_favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String DELETE_REVIEW_SQL = "DELETE FROM movie_reviews WHERE id = ? AND user_id = ?";

  private static final String UPDATE_REVIEW_SQL =
      "UPDATE movie_reviews SET title = ?, director = ?, genre = ?, rating = ?, review = ?, date_watched = ?, ticket_image_path = ?, is_favorite = ? WHERE id = ? AND user_id = ?";

  private static final String SELECT_ALL_REVIEWS_SQL = "SELECT * FROM movie_reviews ORDER BY created_at DESC";

  private static final String SELECT_REVIEWS_BY_USER_SQL =
      "SELECT * FROM movie_reviews WHERE user_id = ? ORDER BY created_at DESC";

  private static final String SELECT_FAVORITE_REVIEWS_SQL =
      "SELECT * FROM movie_reviews WHERE is_favorite = TRUE ORDER BY created_at DESC";

  private static final String SELECT_FAVORITE_REVIEWS_BY_USER_SQL =
      "SELECT * FROM movie_reviews WHERE is_favorite = TRUE AND user_id = ? ORDER BY created_at DESC";

  private static final String UPDATE_FAVORITE_STATUS_SQL =
      "UPDATE movie_reviews SET is_favorite = ? WHERE id = ? AND user_id = ?";

  private static final String DELETE_ALL_REVIEWS_SQL = "DELETE FROM movie_reviews";

  private static final String SELECT_AVG_RATING_SQL = "SELECT AVG(rating) FROM movie_reviews";

  private static final String SELECT_AVG_RATING_BY_USER_SQL =
      "SELECT AVG(rating) FROM movie_reviews WHERE user_id = ?";

  private static final String SELECT_TOTAL_REVIEWS_SQL = "SELECT COUNT(*) FROM movie_reviews";

  private static final String SELECT_TOTAL_REVIEWS_BY_USER_SQL =
      "SELECT COUNT(*) FROM movie_reviews WHERE user_id = ?";

  private static final String SELECT_THEATER_VISIT_COUNT_SQL =
      "SELECT COUNT(*) FROM movie_reviews WHERE ticket_image_path IS NOT NULL AND ticket_image_path <> ''";

  private static final String SELECT_THEATER_VISIT_COUNT_BY_USER_SQL =
      "SELECT COUNT(*) FROM movie_reviews WHERE ticket_image_path IS NOT NULL AND ticket_image_path <> '' AND user_id = ?";

  private final int currentUserId;
  private final List<ReviewChangeListener> listeners = new CopyOnWriteArrayList<>();

  public MovieReviewManager() {
    this(0);
  }

  public MovieReviewManager(int currentUserId) {
    this.currentUserId = currentUserId;
    initializeDatabase();
  }

  public int getCurrentUserId() {
    return currentUserId;
  }

  // Observer registration
  /**
   * Adds a review change listener.
   *
   * @param listener the listener to add
   */
  public void addReviewChangeListener(ReviewChangeListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  /**
   * Removes a review change listener.
   *
   * @param listener the listener to remove
   */
  public void removeReviewChangeListener(ReviewChangeListener listener) {
    if (listener != null) {
      listeners.remove(listener);
    }
  }

  private void notifyReviewAdded(MovieReview review) {
    for (ReviewChangeListener listener : listeners) {
      try {
        listener.onReviewAdded(review);
      } catch (Exception ignored) {
        // Ignore listener exceptions
      }
    }
  }

  private void notifyReviewUpdated(MovieReview review) {
    for (ReviewChangeListener listener : listeners) {
      try {
        listener.onReviewUpdated(review);
      } catch (Exception ignored) {
        // Ignore listener exceptions
      }
    }
  }

  private void notifyReviewDeleted(int reviewId) {
    for (ReviewChangeListener listener : listeners) {
      try {
        listener.onReviewDeleted(reviewId);
      } catch (Exception ignored) {
        // Ignore listener exceptions
      }
    }
  }

  private void notifyReviewsBulkDeleted(int count) {
    for (ReviewChangeListener listener : listeners) {
      try {
        listener.onReviewsBulkDeleted(count);
      } catch (Exception ignored) {
        // Ignore listener exceptions
      }
    }
  }

  private void notifyReviewsCleared() {
    for (ReviewChangeListener listener : listeners) {
      try {
        listener.onReviewsCleared();
      } catch (Exception ignored) {
        // Ignore listener exceptions
      }
    }
  }

  private void initializeDatabase() {
    try (Connection conn = DatabaseConnection.getConnection()) {
      createMovieReviewsTable(conn);
      createUsersTable(conn);
      seedTestUserIfNeeded(conn);
      ensureColumnsExist(conn);
    } catch (SQLException e) {
      System.err.println("Error initializing database: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void createMovieReviewsTable(Connection conn) throws SQLException {
    try (PreparedStatement stmt = conn.prepareStatement(CREATE_MOVIE_REVIEWS_TABLE_SQL)) {
      stmt.executeUpdate();
    }
  }

  private void createUsersTable(Connection conn) throws SQLException {
    try (PreparedStatement stmt = conn.prepareStatement(CREATE_USERS_TABLE_SQL)) {
      stmt.executeUpdate();
    }
  }

  private void seedTestUserIfNeeded(Connection conn) throws SQLException {
    if (currentUserId > 0 && !userExists(conn, currentUserId)) {
      insertTestUser(conn, currentUserId);
    }
  }

  private boolean userExists(Connection conn, int userId) throws SQLException {
    try (PreparedStatement checkStmt = conn.prepareStatement(CHECK_USER_EXISTS_SQL)) {
      checkStmt.setInt(1, userId);
      try (ResultSet rs = checkStmt.executeQuery()) {
        return rs.next() && rs.getInt(1) > 0;
      }
    }
  }

  private void insertTestUser(Connection conn, int userId) throws SQLException {
    try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_TEST_USER_SQL)) {
      insertStmt.setInt(1, userId);
      insertStmt.setString(2, "test_user_" + userId);
      insertStmt.setString(3, "test_" + userId + "@example.com");
      insertStmt.setString(4, "password");
      insertStmt.executeUpdate();
    }
  }

  private void ensureColumnsExist(Connection conn) throws SQLException {
    ensureColumnExists(conn, TABLE_MOVIE_REVIEWS, COLUMN_TICKET_IMAGE_PATH, "VARCHAR(500)");
    ensureColumnExists(conn, TABLE_MOVIE_REVIEWS, COLUMN_IS_FAVORITE, "BOOLEAN DEFAULT FALSE");
  }

  private void ensureColumnExists(
      Connection conn, String tableName, String columnName, String columnDefinition)
      throws SQLException {
    DatabaseMetaData metaData = conn.getMetaData();
    boolean exists =
        columnExists(metaData, tableName, columnName)
            || columnExists(metaData, tableName.toUpperCase(), columnName.toUpperCase());
    if (!exists) {
      String alterSQL =
          "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition;
      try (PreparedStatement alterStmt = conn.prepareStatement(alterSQL)) {
        alterStmt.executeUpdate();
      }
    }
  }

  private boolean columnExists(DatabaseMetaData metaData, String tableName, String columnName)
      throws SQLException {
    try (ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
      return rs.next();
    }
  }

  /**
   * Adds a new movie review.
   *
   * @param movieReview the review to add
   */
  public void addReview(MovieReview movieReview) {
    if (movieReview == null) {
      return;
    }
    prepareReviewForInsert(movieReview);
    insertReviewIntoDatabase(movieReview);
  }

  private void prepareReviewForInsert(MovieReview movieReview) {
    setDefaultValues(movieReview);
    if (currentUserId > 0) {
      movieReview.setUserId(currentUserId);
    }
  }

  private void setDefaultValues(MovieReview movieReview) {
    if (movieReview.getTitle() == null || movieReview.getTitle().trim().isEmpty()) {
      movieReview.setTitle(DEFAULT_TITLE);
    }
    if (movieReview.getDirector() == null || movieReview.getDirector().trim().isEmpty()) {
      movieReview.setDirector(DEFAULT_DIRECTOR);
    }
    if (movieReview.getGenre() == null) {
      movieReview.setGenre(EMPTY_STRING);
    }
    if (movieReview.getReview() == null) {
      movieReview.setReview(EMPTY_STRING);
    }
  }

  private void insertReviewIntoDatabase(MovieReview movieReview) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt =
            conn.prepareStatement(INSERT_REVIEW_SQL, Statement.RETURN_GENERATED_KEYS)) {
      setReviewInsertParameters(stmt, movieReview);
      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected > 0) {
        setGeneratedReviewId(stmt, movieReview);
        notifyReviewAdded(movieReview);
      }
    } catch (SQLException e) {
      System.err.println("Error adding review: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void setReviewInsertParameters(PreparedStatement stmt, MovieReview review)
      throws SQLException {
    stmt.setInt(1, review.getUserId());
    stmt.setString(2, review.getTitle());
    stmt.setString(3, review.getDirector());
    stmt.setString(4, review.getGenre());
    stmt.setDouble(5, review.getRating());
    stmt.setString(6, review.getReview());
    stmt.setDate(7, Date.valueOf(review.getDateWatched()));
    stmt.setString(8, review.getTicketImagePath());
    stmt.setBoolean(9, review.isFavorite());
  }

  private void setGeneratedReviewId(PreparedStatement stmt, MovieReview review)
      throws SQLException {
    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
      if (generatedKeys.next()) {
        review.setId(generatedKeys.getInt(1));
      }
    }
  }

  /**
   * Deletes a movie review.
   *
   * @param review the review to delete
   */
  public void deleteReview(MovieReview review) {
    if (review == null) {
      return;
    }
    enforceUserScope(review);
    deleteReviewFromDatabase(review);
  }

  private void enforceUserScope(MovieReview review) {
    if (currentUserId > 0) {
      review.setUserId(currentUserId);
    }
  }

  private void deleteReviewFromDatabase(MovieReview review) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(DELETE_REVIEW_SQL)) {
      stmt.setInt(1, review.getId());
      stmt.setInt(2, review.getUserId());
      stmt.executeUpdate();
      notifyReviewDeleted(review.getId());
    } catch (SQLException e) {
      System.err.println("Error deleting review: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Deletes multiple reviews in a single operation.
   *
   * @param reviews list of MovieReview objects to delete
   * @return number of reviews successfully deleted
   */
  public int deleteReviews(List<MovieReview> reviews) {
    if (reviews == null || reviews.isEmpty()) {
      return 0;
    }
    List<MovieReview> validReviews = filterAndScopeReviews(reviews);
    if (validReviews.isEmpty()) {
      return 0;
    }
    return deleteReviewsFromDatabase(validReviews);
  }

  private List<MovieReview> filterAndScopeReviews(List<MovieReview> reviews) {
    List<MovieReview> validReviews = new ArrayList<>();
    for (MovieReview review : reviews) {
      if (review != null) {
        enforceUserScope(review);
        validReviews.add(review);
      }
    }
    return validReviews;
  }

  private int deleteReviewsFromDatabase(List<MovieReview> validReviews) {
    String sql = buildBulkDeleteSql(validReviews.size());
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      setBulkDeleteParameters(stmt, validReviews);
      int deletedCount = stmt.executeUpdate();
      if (deletedCount > 0) {
        notifyReviewsBulkDeleted(deletedCount);
      }
      return deletedCount;
    } catch (SQLException e) {
      System.err.println("Error deleting reviews: " + e.getMessage());
      e.printStackTrace();
    }
    return 0;
  }

  private String buildBulkDeleteSql(int reviewCount) {
    StringBuilder sqlBuilder =
        new StringBuilder("DELETE FROM movie_reviews WHERE user_id = ? AND id IN (");
    for (int i = 0; i < reviewCount; i++) {
      if (i > 0) {
        sqlBuilder.append(", ");
      }
      sqlBuilder.append("?");
    }
    sqlBuilder.append(")");
    return sqlBuilder.toString();
  }

  private void setBulkDeleteParameters(PreparedStatement stmt, List<MovieReview> validReviews)
      throws SQLException {
    stmt.setInt(1, validReviews.get(0).getUserId());
    for (int i = 0; i < validReviews.size(); i++) {
      stmt.setInt(i + 2, validReviews.get(i).getId());
    }
  }

  /**
   * Updates a movie review.
   *
   * @param original the original review
   * @param updated the updated review
   */
  public void updateReview(MovieReview original, MovieReview updated) {
    if (original == null || updated == null) {
      return;
    }
    prepareReviewForUpdate(original, updated);
    updateReviewInDatabase(original, updated);
  }

  private void prepareReviewForUpdate(MovieReview original, MovieReview updated) {
    if (currentUserId > 0) {
      original.setUserId(currentUserId);
      updated.setUserId(currentUserId);
    }
    updated.setId(original.getId());
  }

  private void updateReviewInDatabase(MovieReview original, MovieReview updated) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_REVIEW_SQL)) {
      setReviewUpdateParameters(stmt, original, updated);
      stmt.executeUpdate();
      notifyReviewUpdated(updated);
    } catch (SQLException e) {
      System.err.println("Error updating review: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void setReviewUpdateParameters(
      PreparedStatement stmt, MovieReview original, MovieReview updated) throws SQLException {
    stmt.setString(1, updated.getTitle());
    stmt.setString(2, updated.getDirector());
    stmt.setString(3, updated.getGenre());
    stmt.setDouble(4, updated.getRating());
    stmt.setString(5, updated.getReview());
    stmt.setDate(6, Date.valueOf(updated.getDateWatched()));
    stmt.setString(7, updated.getTicketImagePath());
    stmt.setBoolean(8, updated.isFavorite());
    stmt.setInt(9, original.getId());
    stmt.setInt(10, original.getUserId());
  }

  /**
   * Gets all movie reviews.
   *
   * @return list of all movie reviews
   */
  public List<MovieReview> getAllMovies() {
    if (currentUserId > 0) {
      return getReviewsByUser(currentUserId);
    }
    return executeQuery(SELECT_ALL_REVIEWS_SQL);
  }

  private List<MovieReview> getReviewsByUser(int userId) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(SELECT_REVIEWS_BY_USER_SQL)) {
      stmt.setInt(1, userId);
      return executeQuery(stmt);
    } catch (SQLException e) {
      System.err.println("Error getting movies for current user: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * Searches reviews by query string.
   *
   * @param query the search query
   * @return list of matching reviews
   */
  public List<MovieReview> searchReviews(String query) {
    if (query == null || query.trim().isEmpty()) {
      return getAllMovies();
    }
    return executeSearchQuery(query);
  }

  private List<MovieReview> executeSearchQuery(String query) {
    String sql = buildSearchSql();
    String searchPattern = "%" + query.toLowerCase().trim() + "%";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      setSearchParameters(stmt, searchPattern);
      return executeQuery(stmt);
    } catch (SQLException e) {
      System.err.println("Error searching reviews: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private String buildSearchSql() {
    String base = "SELECT * FROM movie_reviews ";
    String where = "WHERE ";
    if (currentUserId > 0) {
      where += "user_id = ? AND ";
    }
    return base
        + where
        + "(LOWER(title) LIKE ? OR LOWER(director) LIKE ? OR LOWER(genre) LIKE ?) ORDER BY created_at DESC";
  }

  private void setSearchParameters(PreparedStatement stmt, String searchPattern)
      throws SQLException {
    int paramIndex = 1;
    if (currentUserId > 0) {
      stmt.setInt(paramIndex++, currentUserId);
    }
    stmt.setString(paramIndex++, searchPattern);
    stmt.setString(paramIndex++, searchPattern);
    stmt.setString(paramIndex, searchPattern);
  }

  /**
   * Gets sorted reviews using the specified sort option.
   *
   * @param sortOption the sort option string
   * @return sorted list of reviews
   */
  public List<MovieReview> getSortedReviews(String sortOption) {
    List<MovieReview> reviews = getAllMovies();
    SortStrategy strategy = SortStrategyFactory.createStrategy(sortOption);
    return strategy.sort(reviews);
  }

  /**
   * Gets favorite reviews.
   *
   * @return list of favorite reviews
   */
  public List<MovieReview> getFavoriteReviews() {
    String sql =
        currentUserId > 0 ? SELECT_FAVORITE_REVIEWS_BY_USER_SQL : SELECT_FAVORITE_REVIEWS_SQL;
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      if (currentUserId > 0) {
        stmt.setInt(1, currentUserId);
      }
      return executeQuery(stmt);
    } catch (SQLException e) {
      System.err.println("Error getting favorite reviews: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * Gets the average rating of all reviews.
   *
   * @return the average rating
   */
  public double getAverageRating() {
    String sql = currentUserId > 0 ? SELECT_AVG_RATING_BY_USER_SQL : SELECT_AVG_RATING_SQL;
    return getDoubleValue(sql, "Error getting average rating");
  }

  /**
   * Gets the total number of reviews.
   *
   * @return the total number of reviews
   */
  public int getTotalReviews() {
    String sql =
        currentUserId > 0 ? SELECT_TOTAL_REVIEWS_BY_USER_SQL : SELECT_TOTAL_REVIEWS_SQL;
    return getIntValue(sql, "Error getting total reviews");
  }

  /**
   * Gets the count of theater visits (reviews with ticket images).
   *
   * @return the theater visit count
   */
  public int getTheaterVisitCount() {
    String sql =
        currentUserId > 0
            ? SELECT_THEATER_VISIT_COUNT_BY_USER_SQL
            : SELECT_THEATER_VISIT_COUNT_SQL;
    return getIntValue(sql, "Error getting theater visit count");
  }

  private double getDoubleValue(String sql, String errorMessage) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      if (currentUserId > 0) {
        stmt.setInt(1, currentUserId);
      }
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getDouble(1);
        }
      }
    } catch (SQLException e) {
      System.err.println(errorMessage + ": " + e.getMessage());
      e.printStackTrace();
    }
    return 0.0;
  }

  private int getIntValue(String sql, String errorMessage) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      if (currentUserId > 0) {
        stmt.setInt(1, currentUserId);
      }
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      System.err.println(errorMessage + ": " + e.getMessage());
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * Sets the favorite status of a review.
   *
   * @param review the review to update
   * @param favorite the favorite status
   */
  public void setFavoriteStatus(MovieReview review, boolean favorite) {
    if (review == null) {
      return;
    }
    enforceUserScope(review);
    updateFavoriteStatusInDatabase(review, favorite);
  }

  private void updateFavoriteStatusInDatabase(MovieReview review, boolean favorite) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_FAVORITE_STATUS_SQL)) {
      stmt.setBoolean(1, favorite);
      stmt.setInt(2, review.getId());
      stmt.setInt(3, review.getUserId());
      stmt.executeUpdate();
      review.setFavorite(favorite);
      notifyReviewUpdated(review);
    } catch (SQLException e) {
      System.err.println("Error updating favorite status: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Clears all reviews (useful for testing).
   */
  public void clearAllReviews() {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_REVIEWS_SQL)) {
      stmt.executeUpdate();
      notifyReviewsCleared();
    } catch (SQLException e) {
      System.err.println("Error clearing reviews: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Gets reviews with tickets.
   *
   * @return the count of reviews with tickets
   */
  public int getReviewsWithTickets() {
    return getTheaterVisitCount();
  }

  private List<MovieReview> executeQuery(String sql) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      return executeQuery(stmt);
    } catch (SQLException e) {
      System.err.println("Error executing query: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private List<MovieReview> executeQuery(PreparedStatement stmt) throws SQLException {
    List<MovieReview> reviews = new ArrayList<>();
    try (ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        reviews.add(createReviewFromResultSet(rs));
      }
    }
    return reviews;
  }

  private MovieReview createReviewFromResultSet(ResultSet rs) throws SQLException {
    return MovieReview.builder()
        .setId(rs.getInt("id"))
        .setUserId(rs.getInt("user_id"))
        .setTitle(rs.getString("title"))
        .setDirector(rs.getString("director"))
        .setGenre(rs.getString("genre"))
        .setRating(rs.getDouble("rating"))
        .setReview(rs.getString("review"))
        .setDateWatched(rs.getDate("date_watched").toLocalDate())
        .setTicketImagePath(rs.getString("ticket_image_path"))
        .setFavorite(rs.getBoolean("is_favorite"))
        .build();
  }
}
