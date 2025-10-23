package com.cpp.moviejournal;

import com.cpp.moviejournal.manager.MovieReviewManager;
import com.cpp.moviejournal.manager.UserManager;
import com.cpp.moviejournal.model.MovieReview;
import com.cpp.moviejournal.model.User;

/**
 * Demonstration class showing how to use the user-specific movie journal functionality.
 * This class demonstrates the one-to-many relationship between users and movie reviews.
 */
public class UserMovieJournalDemo {
    
    public static void main(String[] args) {
        System.out.println("=== User-Specific Movie Journal Demo ===\n");
        
        // Initialize managers
        UserManager userManager = new UserManager();
        
        // Create and register users
        User user1 = new User("john_doe", "john@example.com", "password123");
        User user2 = new User("jane_smith", "jane@example.com", "password456");
        
        System.out.println("Registering users...");
        boolean user1Registered = userManager.registerUser(user1);
        boolean user2Registered = userManager.registerUser(user2);
        
        System.out.println("User 1 registered: " + user1Registered);
        System.out.println("User 2 registered: " + user2Registered);
        System.out.println();
        
        // Login users
        User loggedInUser1 = userManager.loginUser("john_doe", "password123");
        User loggedInUser2 = userManager.loginUser("jane_smith", "password456");
        
        System.out.println("User 1 logged in: " + (loggedInUser1 != null ? loggedInUser1.getUsername() : "Failed"));
        System.out.println("User 2 logged in: " + (loggedInUser2 != null ? loggedInUser2.getUsername() : "Failed"));
        System.out.println();
        
        // Add movie reviews for each user (using per-user scoped managers)
        System.out.println("Adding movie reviews...");
        
        MovieReviewManager movieReviewManagerUser1 = new MovieReviewManager(loggedInUser1.getId());
        MovieReviewManager movieReviewManagerUser2 = new MovieReviewManager(loggedInUser2.getId());

        // User 1's reviews
        MovieReview review1 = new MovieReview(loggedInUser1.getId(), "Inception", "Christopher Nolan", "Sci-Fi", 4.5, "12/15/2023");
        review1.setReview("Mind-bending masterpiece with incredible visuals!");
        movieReviewManagerUser1.addReview(review1);
        
        MovieReview review2 = new MovieReview(loggedInUser1.getId(), "The Dark Knight", "Christopher Nolan", "Action", 5.0, "11/20/2023");
        review2.setReview("Perfect superhero movie with amazing performances.");
        movieReviewManagerUser1.addReview(review2);
        
        // User 2's reviews
        MovieReview review3 = new MovieReview(loggedInUser2.getId(), "Inception", "Christopher Nolan", "Sci-Fi", 4.0, "12/10/2023");
        review3.setReview("Great concept but confusing at times.");
        movieReviewManagerUser2.addReview(review3);
        
        MovieReview review4 = new MovieReview(loggedInUser2.getId(), "Titanic", "James Cameron", "Romance", 4.8, "10/05/2023");
        review4.setReview("Epic love story with beautiful cinematography.");
        movieReviewManagerUser2.addReview(review4);
        
        System.out.println("Movie reviews added successfully!\n");
        
        // Demonstrate user-specific functionality
        System.out.println("=== User-Specific Movie Reviews ===");
        
        // Get all reviews for User 1
        System.out.println("Reviews for " + loggedInUser1.getUsername() + ":");
        var user1Reviews = movieReviewManagerUser1.getAllMovies();
        for (MovieReview review : user1Reviews) {
            System.out.println("- " + review.getTitle() + " (" + review.getDirector() + ") - Rating: " + review.getRating() + "/5");
            System.out.println("  Review: " + review.getReview());
            System.out.println("  Date Watched: " + review.getDateWatchedAsString());
            System.out.println();
        }
        
        // Get all reviews for User 2
        System.out.println("Reviews for " + loggedInUser2.getUsername() + ":");
        var user2Reviews = movieReviewManagerUser2.getAllMovies();
        for (MovieReview review : user2Reviews) {
            System.out.println("- " + review.getTitle() + " (" + review.getDirector() + ") - Rating: " + review.getRating() + "/5");
            System.out.println("  Review: " + review.getReview());
            System.out.println("  Date Watched: " + review.getDateWatchedAsString());
            System.out.println();
        }
        
        // Demonstrate search functionality for specific users
        System.out.println("=== User-Specific Search ===");
        
        System.out.println("Searching for 'Nolan' in " + loggedInUser1.getUsername() + "'s reviews:");
        var user1SearchResults = movieReviewManagerUser1.searchReviews("Nolan");
        for (MovieReview review : user1SearchResults) {
            System.out.println("- " + review.getTitle() + " (" + review.getDirector() + ")");
        }
        System.out.println();
        
        System.out.println("Searching for 'Nolan' in " + loggedInUser2.getUsername() + "'s reviews:");
        var user2SearchResults = movieReviewManagerUser2.searchReviews("Nolan");
        for (MovieReview review : user2SearchResults) {
            System.out.println("- " + review.getTitle() + " (" + review.getDirector() + ")");
        }
        System.out.println();
        
        // Demonstrate sorting for specific users
        System.out.println("=== User-Specific Sorting ===");
        
        System.out.println(loggedInUser1.getUsername() + "'s reviews sorted by rating (high to low):");
        var user1SortedReviews = movieReviewManagerUser1.getSortedReviews("Rating (High)");
        for (MovieReview review : user1SortedReviews) {
            System.out.println("- " + review.getTitle() + " - Rating: " + review.getRating() + "/5");
        }
        System.out.println();
        
        // Demonstrate statistics for specific users
        System.out.println("=== User-Specific Statistics ===");
        
        System.out.println("Statistics for " + loggedInUser1.getUsername() + ":");
        System.out.println("- Total Reviews: " + movieReviewManagerUser1.getTotalReviews());
        System.out.println("- Average Rating: " + String.format("%.2f", movieReviewManagerUser1.getAverageRating()));
        System.out.println();
        
        System.out.println("Statistics for " + loggedInUser2.getUsername() + ":");
        System.out.println("- Total Reviews: " + movieReviewManagerUser2.getTotalReviews());
        System.out.println("- Average Rating: " + String.format("%.2f", movieReviewManagerUser2.getAverageRating()));
        System.out.println();
        
        // Demonstrate that users can only see their own reviews
        System.out.println("=== Data Isolation Verification ===");
        
        System.out.println("All reviews in database (admin view):");
        // Use an unscoped manager to view all reviews (admin view)
        MovieReviewManager adminViewManager = new MovieReviewManager();
        var allReviews = adminViewManager.getAllMovies();
        System.out.println("Total reviews in database: " + allReviews.size());
        for (MovieReview review : allReviews) {
            System.out.println("- " + review.getTitle() + " by User ID: " + review.getUserId());
        }
        System.out.println();
        
        System.out.println("Demo completed successfully!");
        System.out.println("Each user can only see and manage their own movie reviews.");
    }
}
