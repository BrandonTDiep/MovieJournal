# User-Movie Review Relationship Implementation

## Overview
This document describes the implementation of a one-to-many relationship between users and movie reviews in the Movie Journal application. Each user can now login and see only their own movie reviews, ensuring proper data isolation and user-specific functionality.

## Changes Made

### 1. MovieReview Model Updates (`src/main/java/com/cpp/moviejournal/model/MovieReview.java`)

**New Fields Added:**
- `int id` - Primary key for database identification
- `int userId` - Foreign key linking to the users table

**New Constructors:**
- `MovieReview(int userId, String title, String director, String genre, double rating, String dateWatched)` - For creating new reviews with user association
- `MovieReview(int id, int userId, String title, String director, String genre, double rating, String review, LocalDate dateWatched)` - For database retrieval
- Legacy constructor maintained for backward compatibility

**New Methods:**
- `getId()` / `setId(int id)` - Getter/setter for review ID
- `getUserId()` / `setUserId(int userId)` - Getter/setter for user ID

**Updated Methods:**
- `equals()` and `hashCode()` methods now include `id` and `userId` for proper object comparison

### 2. Database Schema Updates (`src/main/java/com/cpp/moviejournal/manager/MovieReviewManager.java`)

**Table Structure Changes:**
```sql
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
```

**Key Changes:**
- Added `user_id` column as foreign key
- Added foreign key constraint with CASCADE delete
- Updated unique constraint to be per-user (allows same movie title/director for different users)

### 3. MovieReviewManager Updates

**Updated Methods:**
- `addReview(MovieReview)` - Now includes `user_id` in INSERT statement and returns generated ID
- `deleteReview(MovieReview)` - Now uses `id` and `user_id` for precise deletion
- `updateReview(MovieReview, MovieReview)` - Now uses `id` and `user_id` for updates
- `executeQuery(PreparedStatement)` - Updated to handle new fields from database

**New User-Specific Methods:**
- `getMoviesByUser(int userId)` - Get all reviews for a specific user
- `searchReviewsByUser(int userId, String query)` - Search reviews for a specific user
- `getSortedReviewsByUser(int userId, String sortOption)` - Get sorted reviews for a specific user
- `getAverageRatingByUser(int userId)` - Get average rating for a specific user
- `getTotalReviewsByUser(int userId)` - Get total review count for a specific user

## Usage Examples

### Creating a User-Specific Review
```java
// Login user
UserManager userManager = new UserManager();
User user = userManager.loginUser("username", "password");

// Create review with user association
MovieReview review = new MovieReview(user.getId(), "Movie Title", "Director", "Genre", 4.5, "12/15/2023");
review.setReview("Great movie!");

// Add to database
MovieReviewManager movieManager = new MovieReviewManager();
movieManager.addReview(review);
```

### Retrieving User-Specific Reviews
```java
// Get all reviews for logged-in user
List<MovieReview> userReviews = movieManager.getMoviesByUser(user.getId());

// Search user's reviews
List<MovieReview> searchResults = movieManager.searchReviewsByUser(user.getId(), "Nolan");

// Get sorted reviews
List<MovieReview> sortedReviews = movieManager.getSortedReviewsByUser(user.getId(), "Rating (High)");
```

### User Statistics
```java
// Get user-specific statistics
int totalReviews = movieManager.getTotalReviewsByUser(user.getId());
double averageRating = movieManager.getAverageRatingByUser(user.getId());
```

## Security and Data Isolation

### Key Security Features:
1. **Foreign Key Constraints** - Ensures data integrity between users and reviews
2. **CASCADE Delete** - When a user is deleted, all their reviews are automatically removed
3. **User-Specific Queries** - All new methods filter by `user_id` to prevent cross-user data access
4. **Unique Constraints** - Prevents duplicate reviews per user while allowing same movie for different users

### Data Isolation:
- Users can only see their own reviews through the new user-specific methods
- The original global methods (`getAllMovies()`, `searchReviews()`) still exist for admin purposes
- Database operations now require both review ID and user ID for updates/deletes

## Migration Considerations

### For Existing Data:
- Existing reviews will need to be migrated to include a `user_id`
- Consider creating a default user for existing reviews or assigning them to specific users
- The legacy constructor sets `userId = 0` for backward compatibility

### For New Implementations:
- Always use the new constructor with `userId` parameter
- Use user-specific methods for normal application functionality
- Reserve global methods for administrative purposes only

## Demo Application

A complete demonstration is provided in `UserMovieJournalDemo.java` that shows:
- User registration and login
- Creating user-specific reviews
- Retrieving user-specific data
- Searching and sorting user reviews
- User-specific statistics
- Data isolation verification

## Benefits

1. **Data Security** - Users can only access their own reviews
2. **Scalability** - Supports multiple users with isolated data
3. **User Experience** - Each user sees only their personal movie journal
4. **Data Integrity** - Foreign key constraints ensure referential integrity
5. **Flexibility** - Maintains backward compatibility while adding new functionality

This implementation provides a solid foundation for a multi-user movie journal application where each user maintains their own private collection of movie reviews.
