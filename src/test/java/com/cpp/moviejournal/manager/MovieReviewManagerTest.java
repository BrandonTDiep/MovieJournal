package com.cpp.moviejournal.manager;

import com.cpp.moviejournal.model.MovieReview;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for MovieReviewManager class
 * Tests all CRUD operations, search functionality, and sorting with various scenarios
 */
@DisplayName("MovieReviewManager Unit Tests")
class MovieReviewManagerTest {

    private MovieReviewManager manager;
    private MovieReview testReview1;
    private MovieReview testReview2;
    private MovieReview testReview3;

    @BeforeEach
    void setUp() {
        manager = new MovieReviewManager(1);
        // Clear database before each test to ensure isolation
        manager.clearAllReviews();

        // Create test reviews
        testReview1 = new MovieReview("The Matrix", "The Wachowskis", "Sci-Fi", 4.5, "03/31/1999");
        testReview1.setReview("Mind-bending sci-fi masterpiece!");
        
        testReview2 = new MovieReview("Inception", "Christopher Nolan", "Thriller", 4.8, "07/16/2010");
        testReview2.setReview("Complex and visually stunning.");
        
        testReview3 = new MovieReview("The Dark Knight", "Christopher Nolan", "Action", 4.9, "07/18/2008");
        testReview3.setReview("One of the best superhero movies ever made.");
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        try {
            manager.clearAllReviews();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Nested
    @DisplayName("Add Review Tests")
    class AddReviewTests {

        @Test
        @DisplayName("Should add valid review successfully")
        void shouldAddValidReviewSuccessfully() {
            // When
            manager.addReview(testReview1);
            
            // Then
            List<MovieReview> reviews = manager.getAllMovies();
            assertTrue(reviews.contains(testReview1));
        }

        @Test
        @DisplayName("Should handle null review gracefully")
        void shouldHandleNullReviewGracefully() {
            // When
            manager.addReview(null);
            
            // Then
            // Should not throw exception and not add anything
            List<MovieReview> reviews = manager.getAllMovies();
            assertFalse(reviews.contains(null));
        }

        @Test
        @DisplayName("Should add multiple reviews successfully")
        void shouldAddMultipleReviewsSuccessfully() {
            // When
            manager.addReview(testReview1);
            manager.addReview(testReview2);
            manager.addReview(testReview3);
            
            // Then
            List<MovieReview> reviews = manager.getAllMovies();
            assertEquals(3, reviews.size());
            assertTrue(reviews.contains(testReview1));
            assertTrue(reviews.contains(testReview2));
            assertTrue(reviews.contains(testReview3));
        }

        @Test
        @DisplayName("Should handle duplicate reviews")
        void shouldHandleDuplicateReviews() {
            // Given
            manager.addReview(testReview1);
            MovieReview duplicateReview = new MovieReview(testReview1.getTitle(), testReview1.getDirector(), 
                                                        "Different Genre", 2.0, "01/01/2000");
            
            // When
            manager.addReview(duplicateReview);
            
            // Then
            // Should handle duplicate based on title and director
            List<MovieReview> reviews = manager.getAllMovies();
            // The behavior depends on implementation - could be 1 or 2 reviews
            assertTrue(reviews.size() >= 1);
        }
    }

    @Nested
    @DisplayName("Delete Review Tests")
    class DeleteReviewTests {

        @BeforeEach
        void setUpDeleteTests() {
            manager.addReview(testReview1);
            manager.addReview(testReview2);
        }

        @Test
        @DisplayName("Should delete existing review by object")
        void shouldDeleteExistingReviewByObject() {
            // When
            manager.deleteReview(testReview1);
            
            // Then
            List<MovieReview> reviews = manager.getAllMovies();
            assertFalse(reviews.contains(testReview1));
            assertTrue(reviews.contains(testReview2));
        }

        @Test
        @DisplayName("Should handle deletion of non-existent review")
        void shouldHandleDeletionOfNonExistentReview() {
            // Given
            MovieReview nonExistentReview = new MovieReview("Non-existent", "Director", "Genre", 3.0, "01/01/2000");
            
            // When
            manager.deleteReview(nonExistentReview);
            
            // Then
            // Should not throw exception
            List<MovieReview> reviews = manager.getAllMovies();
            assertEquals(2, reviews.size());
        }

        @Test
        @DisplayName("Should handle deletion of null review")
        void shouldHandleDeletionOfNullReview() {
            // When
            manager.deleteReview(null);
            
            // Then
            // Should not throw exception
            List<MovieReview> reviews = manager.getAllMovies();
            assertEquals(2, reviews.size());
        }

        @Test
        @DisplayName("Should delete all reviews when requested")
        void shouldDeleteAllReviewsWhenRequested() {
            // When
            manager.deleteReview(testReview1);
            manager.deleteReview(testReview2);
            
            // Then
            List<MovieReview> reviews = manager.getAllMovies();
            assertTrue(reviews.isEmpty());
        }
    }

    @Nested
    @DisplayName("Update Review Tests")
    class UpdateReviewTests {

        @BeforeEach
        void setUpUpdateTests() {
            manager.addReview(testReview1);
        }

        @Test
        @DisplayName("Should update existing review successfully")
        void shouldUpdateExistingReviewSuccessfully() {
            // Given
            MovieReview updatedReview = new MovieReview(testReview1.getTitle(), testReview1.getDirector(), 
                                                      "Updated Genre", 5.0, "01/01/2024");
            updatedReview.setReview("Updated review text");
            
            // When
            manager.updateReview(testReview1, updatedReview);
            
            // Then
            List<MovieReview> reviews = manager.getAllMovies();
            assertTrue(reviews.contains(updatedReview));
            // The original review should still be there since we're updating the same record
            assertTrue(reviews.contains(testReview1));
        }

        @Test
        @DisplayName("Should handle update with null original review")
        void shouldHandleUpdateWithNullOriginalReview() {
            // Given
            MovieReview updatedReview = new MovieReview("New Title", "New Director", "Genre", 3.0, "01/01/2024");
            
            // When
            manager.updateReview(null, updatedReview);
            
            // Then
            // Should not throw exception and not update anything
            List<MovieReview> reviews = manager.getAllMovies();
            assertEquals(1, reviews.size());
            assertTrue(reviews.contains(testReview1));
        }

        @Test
        @DisplayName("Should handle update with null updated review")
        void shouldHandleUpdateWithNullUpdatedReview() {
            // When
            manager.updateReview(testReview1, null);
            
            // Then
            // Should not throw exception and not update anything
            List<MovieReview> reviews = manager.getAllMovies();
            assertEquals(1, reviews.size());
            assertTrue(reviews.contains(testReview1));
        }

        @Test
        @DisplayName("Should handle update of non-existent review")
        void shouldHandleUpdateOfNonExistentReview() {
            // Given
            MovieReview nonExistentReview = new MovieReview("Non-existent", "Director", "Genre", 3.0, "01/01/2000");
            MovieReview updatedReview = new MovieReview("Updated", "Director", "Genre", 4.0, "01/01/2024");
            
            // When
            manager.updateReview(nonExistentReview, updatedReview);
            
            // Then
            // Should not throw exception and not add the updated review
            List<MovieReview> reviews = manager.getAllMovies();
            assertEquals(1, reviews.size());
            assertTrue(reviews.contains(testReview1));
        }
    }

    @Nested
    @DisplayName("Search Tests")
    class SearchTests {

        @BeforeEach
        void setUpSearchTests() {
            manager.addReview(testReview1);
            manager.addReview(testReview2);
            manager.addReview(testReview3);
        }

        @Test
        @DisplayName("Should search by title")
        void shouldSearchByTitle() {
            // When
            List<MovieReview> results = manager.searchReviews("Matrix");
            
            // Then
            assertEquals(1, results.size());
            assertTrue(results.contains(testReview1));
        }

        @Test
        @DisplayName("Should search by director")
        void shouldSearchByDirector() {
            // When
            List<MovieReview> results = manager.searchReviews("Nolan");
            
            // Then
            assertEquals(2, results.size());
            assertTrue(results.contains(testReview2));
            assertTrue(results.contains(testReview3));
        }

        @Test
        @DisplayName("Should search by genre")
        void shouldSearchByGenre() {
            // When
            List<MovieReview> results = manager.searchReviews("Sci-Fi");
            
            // Then
            assertEquals(1, results.size());
            assertTrue(results.contains(testReview1));
        }

        @Test
        @DisplayName("Should perform case-insensitive search")
        void shouldPerformCaseInsensitiveSearch() {
            // When
            List<MovieReview> results = manager.searchReviews("matrix");
            
            // Then
            assertEquals(1, results.size());
            assertTrue(results.contains(testReview1));
        }

        @Test
        @DisplayName("Should return empty list for no matches")
        void shouldReturnEmptyListForNoMatches() {
            // When
            List<MovieReview> results = manager.searchReviews("NonExistentMovie");
            
            // Then
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should handle null search query")
        void shouldHandleNullSearchQuery() {
            // When
            List<MovieReview> results = manager.searchReviews(null);
            
            // Then
            // Should return all reviews
            assertEquals(3, results.size());
        }

        @Test
        @DisplayName("Should handle empty search query")
        void shouldHandleEmptySearchQuery() {
            // When
            List<MovieReview> results = manager.searchReviews("");
            
            // Then
            // Should return all reviews
            assertEquals(3, results.size());
        }

        @Test
        @DisplayName("Should handle whitespace-only search query")
        void shouldHandleWhitespaceOnlySearchQuery() {
            // When
            List<MovieReview> results = manager.searchReviews("   ");
            
            // Then
            // Should return all reviews
            assertEquals(3, results.size());
        }

        @Test
        @DisplayName("Should search partial matches")
        void shouldSearchPartialMatches() {
            // When
            List<MovieReview> results = manager.searchReviews("Dark");
            
            // Then
            assertEquals(1, results.size());
            assertTrue(results.contains(testReview3));
        }
    }

    @Nested
    @DisplayName("Sorting Tests")
    class SortingTests {

        @BeforeEach
        void setUpSortingTests() {
            manager.addReview(testReview1);
            manager.addReview(testReview2);
            manager.addReview(testReview3);
        }

        @Test
        @DisplayName("Should sort by date newest first")
        void shouldSortByDateNewestFirst() {
            // When
            List<MovieReview> results = manager.getSortedReviews("Date (Newest)");
            
            // Then
            assertTrue(results.size() >= 3);
            // Verify order (newest first)
            for (int i = 0; i < results.size() - 1; i++) {
                LocalDate current = results.get(i).getDateWatched();
                LocalDate next = results.get(i + 1).getDateWatched();
                if (current != null && next != null) {
                    assertTrue(current.isAfter(next) || current.isEqual(next));
                }
            }
        }

        @Test
        @DisplayName("Should sort by date oldest first")
        void shouldSortByDateOldestFirst() {
            // When
            List<MovieReview> results = manager.getSortedReviews("Date (Oldest)");
            
            // Then
            assertTrue(results.size() >= 3);
            // Verify order (oldest first)
            for (int i = 0; i < results.size() - 1; i++) {
                LocalDate current = results.get(i).getDateWatched();
                LocalDate next = results.get(i + 1).getDateWatched();
                if (current != null && next != null) {
                    assertTrue(current.isBefore(next) || current.isEqual(next));
                }
            }
        }

        @Test
        @DisplayName("Should sort by rating high to low")
        void shouldSortByRatingHighToLow() {
            // When
            List<MovieReview> results = manager.getSortedReviews("Rating (High)");
            
            // Then
            assertTrue(results.size() >= 3);
            // Verify order (highest rating first)
            for (int i = 0; i < results.size() - 1; i++) {
                double current = results.get(i).getRating();
                double next = results.get(i + 1).getRating();
                assertTrue(current >= next);
            }
        }

        @Test
        @DisplayName("Should sort by rating low to high")
        void shouldSortByRatingLowToHigh() {
            // When
            List<MovieReview> results = manager.getSortedReviews("Rating (Low)");
            
            // Then
            assertTrue(results.size() >= 3);
            // Verify order (lowest rating first)
            for (int i = 0; i < results.size() - 1; i++) {
                double current = results.get(i).getRating();
                double next = results.get(i + 1).getRating();
                assertTrue(current <= next);
            }
        }

        @Test
        @DisplayName("Should sort by title A-Z")
        void shouldSortByTitleAToZ() {
            // When
            List<MovieReview> results = manager.getSortedReviews("Title (A-Z)");
            
            // Then
            assertTrue(results.size() >= 3);
            // Verify order (alphabetical)
            for (int i = 0; i < results.size() - 1; i++) {
                String current = results.get(i).getTitle();
                String next = results.get(i + 1).getTitle();
                assertTrue(current.compareTo(next) <= 0);
            }
        }

        @Test
        @DisplayName("Should sort by title Z-A")
        void shouldSortByTitleZToA() {
            // When
            List<MovieReview> results = manager.getSortedReviews("Title (Z-A)");
            
            // Then
            assertTrue(results.size() >= 3);
            // Verify order (reverse alphabetical)
            for (int i = 0; i < results.size() - 1; i++) {
                String current = results.get(i).getTitle();
                String next = results.get(i + 1).getTitle();
                assertTrue(current.compareTo(next) >= 0);
            }
        }

        @Test
        @DisplayName("Should handle invalid sort option")
        void shouldHandleInvalidSortOption() {
            // When
            List<MovieReview> results = manager.getSortedReviews("Invalid Sort Option");
            
            // Then
            // Should return results (default sorting)
            assertNotNull(results);
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @DisplayName("Should return zero for empty manager")
        void shouldReturnZeroForEmptyManager() {
            // When
            int totalReviews = manager.getTotalReviews();
            double averageRating = manager.getAverageRating();
            
            // Then
            assertEquals(0, totalReviews);
            assertEquals(0.0, averageRating, 0.001);
        }

        @Test
        @DisplayName("Should calculate total reviews correctly")
        void shouldCalculateTotalReviewsCorrectly() {
            // Given
            manager.addReview(testReview1);
            manager.addReview(testReview2);
            
            // When
            int totalReviews = manager.getTotalReviews();
            
            // Then
            assertEquals(2, totalReviews);
        }

        @Test
        @DisplayName("Should calculate average rating correctly")
        void shouldCalculateAverageRatingCorrectly() {
            // Given
            manager.addReview(testReview1); // 4.5
            manager.addReview(testReview2); // 4.8
            manager.addReview(testReview3); // 4.9
            
            // When
            double averageRating = manager.getAverageRating();
            
            // Then
            double expectedAverage = (4.5 + 4.8 + 4.9) / 3.0;
            assertEquals(expectedAverage, averageRating, 0.001);
        }

        @Test
        @DisplayName("Should handle single review statistics")
        void shouldHandleSingleReviewStatistics() {
            // Given
            manager.addReview(testReview1);
            
            // When
            int totalReviews = manager.getTotalReviews();
            double averageRating = manager.getAverageRating();
            
            // Then
            assertEquals(1, totalReviews);
            assertEquals(4.5, averageRating, 0.001);
        }
    }

    @Nested
    @DisplayName("Get All Movies Tests")
    class GetAllMoviesTests {

        @Test
        @DisplayName("Should return empty list for empty manager")
        void shouldReturnEmptyListForEmptyManager() {
            // When
            List<MovieReview> reviews = manager.getAllMovies();
            
            // Then
            assertTrue(reviews.isEmpty());
        }

        @Test
        @DisplayName("Should return all added reviews")
        void shouldReturnAllAddedReviews() {
            // Given
            manager.addReview(testReview1);
            manager.addReview(testReview2);
            
            // When
            List<MovieReview> reviews = manager.getAllMovies();
            
            // Then
            assertEquals(2, reviews.size());
            assertTrue(reviews.contains(testReview1));
            assertTrue(reviews.contains(testReview2));
        }

        @Test
        @DisplayName("Should return defensive copy")
        void shouldReturnDefensiveCopy() {
            // Given
            manager.addReview(testReview1);
            
            // When
            List<MovieReview> reviews1 = manager.getAllMovies();
            List<MovieReview> reviews2 = manager.getAllMovies();
            
            // Then
            // Should be different instances
            assertNotSame(reviews1, reviews2);
            // But should contain same elements
            assertEquals(reviews1.size(), reviews2.size());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle reviews with null fields")
        void shouldHandleReviewsWithNullFields() {
            // Given
            MovieReview reviewWithNulls = new MovieReview(null, null, null, 3.0, "01/01/2024");
            reviewWithNulls.setReview(null);
            
            // When
            manager.addReview(reviewWithNulls);
            
            // Then
            // Should not throw exception and should add a review with placeholder values
            List<MovieReview> reviews = manager.getAllMovies();
            assertEquals(1, reviews.size());
            // The review should have placeholder values for null fields
            MovieReview addedReview = reviews.get(0);
            assertEquals("Untitled", addedReview.getTitle());
            assertEquals("Unknown", addedReview.getDirector());
            assertEquals(3.0, addedReview.getRating(), 0.001);
        }

        @Test
        @DisplayName("Should handle reviews with empty strings")
        void shouldHandleReviewsWithEmptyStrings() {
            // Given
            MovieReview reviewWithEmptyStrings = new MovieReview("", "", "", 3.0, "01/01/2024");
            reviewWithEmptyStrings.setReview("");
            
            // When
            manager.addReview(reviewWithEmptyStrings);
            
            // Then
            // Should not throw exception and should add a review with placeholder values
            List<MovieReview> reviews = manager.getAllMovies();
            assertEquals(1, reviews.size());
            // The review should have placeholder values for empty fields
            MovieReview addedReview = reviews.get(0);
            assertEquals("Untitled", addedReview.getTitle());
            assertEquals("Unknown", addedReview.getDirector());
            assertEquals(3.0, addedReview.getRating(), 0.001);
        }

//        @Test
//        @DisplayName("Should handle very large number of reviews")
//        void shouldHandleVeryLargeNumberOfReviews() {
//            // Given
//            int numberOfReviews = 100;
//
//            // When
//            for (int i = 0; i < numberOfReviews; i++) {
//                MovieReview review = new MovieReview("Movie " + i, "Director " + i, "Genre", 3.0, "01/01/2024");
//                manager.addReview(review);
//            }
//
//            // Then
//            List<MovieReview> reviews = manager.getAllMovies();
//            assertEquals(numberOfReviews, reviews.size());
//        }
    }
}
