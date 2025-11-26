package com.cpp.moviejournal.manager;

import com.cpp.moviejournal.model.MovieReview;
import com.cpp.moviejournal.strategy.SortStrategy;
import com.cpp.moviejournal.strategy.SortStrategyFactory;
import com.cpp.moviejournal.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MovieReviewManager {
    
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
    public void addReviewChangeListener(ReviewChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeReviewChangeListener(ReviewChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void notifyReviewAdded(MovieReview review) {
        for (ReviewChangeListener l : listeners) {
            try { l.onReviewAdded(review); } catch (Exception ignored) { }
        }
    }

    private void notifyReviewUpdated(MovieReview review) {
        for (ReviewChangeListener l : listeners) {
            try { l.onReviewUpdated(review); } catch (Exception ignored) { }
        }
    }

    private void notifyReviewDeleted(int reviewId) {
        for (ReviewChangeListener l : listeners) {
            try { l.onReviewDeleted(reviewId); } catch (Exception ignored) { }
        }
    }

    private void notifyReviewsBulkDeleted(int count) {
        for (ReviewChangeListener l : listeners) {
            try { l.onReviewsBulkDeleted(count); } catch (Exception ignored) { }
        }
    }

    private void notifyReviewsCleared() {
        for (ReviewChangeListener l : listeners) {
            try { l.onReviewsCleared(); } catch (Exception ignored) { }
        }
    }

    private void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Create table if it doesn't exist
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS movie_reviews (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    title VARCHAR(255) NOT NULL,
                    director VARCHAR(255) NOT NULL,
                    genre VARCHAR(100) NOT NULL,
                    rating DECIMAL(2,1) NOT NULL CHECK (rating >= 0 AND rating <= 5),
                    review TEXT,
                    date_watched DATE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    UNIQUE KEY unique_user_movie_director (user_id, title, director)
                )
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
                stmt.executeUpdate();
            }

            // Ensure users table exists (needed for FK) and seed current user for tests/demo when scoped
            String createUsersSQL = """
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

            try (PreparedStatement stmt = conn.prepareStatement(createUsersSQL)) {
                stmt.executeUpdate();
            }

            // Seed a default user matching currentUserId if provided and not present
            if (currentUserId > 0) {
                String checkUserSql = "SELECT COUNT(*) FROM users WHERE id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {
                    checkStmt.setInt(1, currentUserId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        boolean exists = false;
                        if (rs.next()) {
                            exists = rs.getInt(1) > 0;
                        }
                        if (!exists) {
                            String insertUserSql = "INSERT INTO users (id, username, email, password, created_at, is_active) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, TRUE)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserSql)) {
                                insertStmt.setInt(1, currentUserId);
                                insertStmt.setString(2, "test_user_" + currentUserId);
                                insertStmt.setString(3, "test_" + currentUserId + "@example.com");
                                insertStmt.setString(4, "password");
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addReview(MovieReview movieReview) {
        if (movieReview == null) return;
        
        // Handle null values by providing defaults
        String title = movieReview.getTitle() != null ? movieReview.getTitle() : "";
        String director = movieReview.getDirector() != null ? movieReview.getDirector() : "";
        String genre = movieReview.getGenre() != null ? movieReview.getGenre() : "";
        String review = movieReview.getReview() != null ? movieReview.getReview() : "";
        
        // Use placeholder values for empty title/director to satisfy database constraints
        if (title.trim().isEmpty()) {
            title = "Untitled";
        }
        if (director.trim().isEmpty()) {
            director = "Unknown";
        }
        
        // Ensure the review is associated with the current user context if provided
        if (currentUserId > 0) {
            movieReview.setUserId(currentUserId);
        }
        
        String sql = "INSERT INTO movie_reviews (user_id, title, director, genre, rating, review, date_watched) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, movieReview.getUserId());
            stmt.setString(2, title);
            stmt.setString(3, director);
            stmt.setString(4, genre);
            stmt.setDouble(5, movieReview.getRating());
            stmt.setString(6, review);
            stmt.setDate(7, Date.valueOf(movieReview.getDateWatched()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        movieReview.setId(generatedKeys.getInt(1));
                    }
                }
                notifyReviewAdded(movieReview);
            }
        } catch (SQLException e) {
            System.err.println("Error adding review: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteReview(MovieReview review) {
        if (review == null) return;
        
        // Enforce current user scope on the operation
        if (currentUserId > 0) {
            review.setUserId(currentUserId);
        }
        
        String sql = "DELETE FROM movie_reviews WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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
     * Deletes multiple reviews in a single operation
     * @param reviews List of MovieReview objects to delete
     * @return Number of reviews successfully deleted
     */
    public int deleteReviews(List<MovieReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }
        
        // Filter out null reviews and ensure user scope
        List<MovieReview> validReviews = new ArrayList<>();
        for (MovieReview review : reviews) {
            if (review != null) {
                // Enforce current user scope on the operation
                if (currentUserId > 0) {
                    review.setUserId(currentUserId);
                }
                validReviews.add(review);
            }
        }
        
        if (validReviews.isEmpty()) {
            return 0;
        }
        
        // Build the SQL query with placeholders for all reviews
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM movie_reviews WHERE user_id = ? AND id IN (");
        for (int i = 0; i < validReviews.size(); i++) {
            if (i > 0) sqlBuilder.append(", ");
            sqlBuilder.append("?");
        }
        sqlBuilder.append(")");
        
        String sql = sqlBuilder.toString();
        int deletedCount = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set user_id parameter
            stmt.setInt(1, validReviews.get(0).getUserId());
            
            // Set review ID parameters
            for (int i = 0; i < validReviews.size(); i++) {
                stmt.setInt(i + 2, validReviews.get(i).getId());
            }
            
            deletedCount = stmt.executeUpdate();
            if (deletedCount > 0) {
                notifyReviewsBulkDeleted(deletedCount);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting reviews: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deletedCount;
    }

    public void updateReview(MovieReview original, MovieReview updated) {
        if (original == null || updated == null) return;
        
        // Enforce current user scope on the operation
        if (currentUserId > 0) {
            original.setUserId(currentUserId);
            updated.setUserId(currentUserId);
        }
        // Ensure the updated review carries the same id as the original for equality checks in tests
        updated.setId(original.getId());
        
        String sql = "UPDATE movie_reviews SET title = ?, director = ?, genre = ?, rating = ?, review = ?, date_watched = ? WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, updated.getTitle());
            stmt.setString(2, updated.getDirector());
            stmt.setString(3, updated.getGenre());
            stmt.setDouble(4, updated.getRating());
            stmt.setString(5, updated.getReview());
            stmt.setDate(6, Date.valueOf(updated.getDateWatched()));
            stmt.setInt(7, original.getId());
            stmt.setInt(8, original.getUserId());
            
            stmt.executeUpdate();
            notifyReviewUpdated(updated);
        } catch (SQLException e) {
            System.err.println("Error updating review: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<MovieReview> getAllMovies() {
        if (currentUserId > 0) {
            String sql = "SELECT * FROM movie_reviews WHERE user_id = ? ORDER BY created_at DESC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentUserId);
                return executeQuery(stmt);
            } catch (SQLException e) {
                System.err.println("Error getting movies for current user: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        String sql = "SELECT * FROM movie_reviews ORDER BY created_at DESC";
        return executeQuery(sql);
    }

    public List<MovieReview> searchReviews(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllMovies();
        }

        String base = "SELECT * FROM movie_reviews ";
        String where = "WHERE ";
        if (currentUserId > 0) {
            where += "user_id = ? AND ";
        }
        String sql = base + where + "(LOWER(title) LIKE ? OR LOWER(director) LIKE ? OR LOWER(genre) LIKE ?) ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query.toLowerCase().trim() + "%";
            int paramIndex = 1;
            if (currentUserId > 0) {
                stmt.setInt(paramIndex++, currentUserId);
            }
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex, searchPattern);
            
            return executeQuery(stmt);
        } catch (SQLException e) {
            System.err.println("Error searching reviews: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<MovieReview> getSortedReviews(String sortOption) {
        // Strategy Pattern: Use strategy to sort reviews
        List<MovieReview> reviews = getAllMovies();
        SortStrategy strategy = SortStrategyFactory.createStrategy(sortOption);
        return strategy.sort(reviews);
    }


    public double getAverageRating() {
        if (currentUserId > 0) {
            String sql = "SELECT AVG(rating) FROM movie_reviews WHERE user_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentUserId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble(1);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error getting average rating: " + e.getMessage());
                e.printStackTrace();
            }
            return 0.0;
        }
        String sql = "SELECT AVG(rating) FROM movie_reviews";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting average rating: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }


    public int getTotalReviews() {
        if (currentUserId > 0) {
            String sql = "SELECT COUNT(*) FROM movie_reviews WHERE user_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentUserId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error getting total reviews: " + e.getMessage());
                e.printStackTrace();
            }
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM movie_reviews";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total reviews: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    // Method to clear all reviews (useful for testing)
    public void clearAllReviews() {
        String sql = "DELETE FROM movie_reviews";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            notifyReviewsCleared();
        } catch (SQLException e) {
            System.err.println("Error clearing reviews: " + e.getMessage());
            e.printStackTrace();
        }
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
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String title = rs.getString("title");
                String director = rs.getString("director");
                String genre = rs.getString("genre");
                double rating = rs.getDouble("rating");
                String reviewText = rs.getString("review");
                LocalDate dateWatched = rs.getDate("date_watched").toLocalDate();
                
                MovieReview review = new MovieReview(id, userId, title, director, genre, rating, 
                    reviewText != null ? reviewText : "", dateWatched);
                
                reviews.add(review);
            }
        }
        
        return reviews;
    }
}