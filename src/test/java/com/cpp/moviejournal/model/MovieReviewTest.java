package com.cpp.moviejournal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for MovieReview class
 * Tests all methods with valid and invalid inputs, edge cases, and exception handling
 */
@DisplayName("MovieReview Unit Tests")
class MovieReviewTest {

    private MovieReview validReview;
    private static final String VALID_TITLE = "The Matrix";
    private static final String VALID_DIRECTOR = "The Wachowskis";
    private static final String VALID_GENRE = "Sci-Fi";
    private static final double VALID_RATING = 4.5;
    private static final String VALID_DATE = "03/31/1999";

    @BeforeEach
    void setUp() {
        validReview = new MovieReview(VALID_TITLE, VALID_DIRECTOR, VALID_GENRE, VALID_RATING, VALID_DATE);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create MovieReview with valid parameters")
        void shouldCreateMovieReviewWithValidParameters() {
            // Given & When
            MovieReview review = new MovieReview("Inception", "Christopher Nolan", "Thriller", 4.8, "07/16/2010");
            
            // Then
            assertNotNull(review);
            assertEquals("Inception", review.getTitle());
            assertEquals("Christopher Nolan", review.getDirector());
            assertEquals("Thriller", review.getGenre());
            assertEquals(4.8, review.getRating(), 0.001);
            assertEquals("", review.getReview()); // Should be empty by default
            assertNotNull(review.getDateWatched());
        }

        @Test
        @DisplayName("Should handle valid date format MM/dd/yyyy")
        void shouldHandleValidDateFormat() {
            // Given & When
            MovieReview review = new MovieReview("Test Movie", "Test Director", "Test Genre", 3.0, "12/25/2023");
            
            // Then
            LocalDate expectedDate = LocalDate.of(2023, 12, 25);
            assertEquals(expectedDate, review.getDateWatched());
        }

        @Test
        @DisplayName("Should handle single digit month and day")
        void shouldHandleSingleDigitMonthAndDay() {
            // Given & When
            MovieReview review = new MovieReview("Test Movie", "Test Director", "Test Genre", 3.0, "1/5/2023");
            
            // Then
            LocalDate expectedDate = LocalDate.of(2023, 1, 5);
            assertEquals(expectedDate, review.getDateWatched());
        }

        @Test
        @DisplayName("Should default to current date for invalid date format")
        void shouldDefaultToCurrentDateForInvalidDateFormat() {
            // Given & When
            MovieReview review = new MovieReview("Test Movie", "Test Director", "Test Genre", 3.0, "invalid-date");
            
            // Then
            assertNotNull(review.getDateWatched());
            // Should be today's date (within reasonable range)
            LocalDate today = LocalDate.now();
            assertTrue(review.getDateWatched().isEqual(today) || 
                      review.getDateWatched().isAfter(today.minusDays(1)));
        }

        @Test
        @DisplayName("Should default to current date for malformed date string")
        void shouldDefaultToCurrentDateForMalformedDateString() {
            // Given & When
            MovieReview review = new MovieReview("Test Movie", "Test Director", "Test Genre", 3.0, "13/45/2023");
            
            // Then
            assertNotNull(review.getDateWatched());
            LocalDate today = LocalDate.now();
            assertTrue(review.getDateWatched().isEqual(today) || 
                      review.getDateWatched().isAfter(today.minusDays(1)));
        }

        @Test
        @DisplayName("Should handle empty date string")
        void shouldHandleEmptyDateString() {
            // Given & When
            MovieReview review = new MovieReview("Test Movie", "Test Director", "Test Genre", 3.0, "");
            
            // Then
            assertNotNull(review.getDateWatched());
            LocalDate today = LocalDate.now();
            assertTrue(review.getDateWatched().isEqual(today) || 
                      review.getDateWatched().isAfter(today.minusDays(1)));
        }

        @Test
        @DisplayName("Should handle null date string")
        void shouldHandleNullDateString() {
            // Given & When
            MovieReview review = new MovieReview("Test Movie", "Test Director", "Test Genre", 3.0, null);
            
            // Then
            assertNotNull(review.getDateWatched());
            LocalDate today = LocalDate.now();
            assertTrue(review.getDateWatched().isEqual(today) || 
                      review.getDateWatched().isAfter(today.minusDays(1)));
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set title correctly")
        void shouldGetAndSetTitleCorrectly() {
            // Given
            String newTitle = "New Title";
            
            // When
            validReview.setTitle(newTitle);
            
            // Then
            assertEquals(newTitle, validReview.getTitle());
        }

        @Test
        @DisplayName("Should handle null title")
        void shouldHandleNullTitle() {
            // When
            validReview.setTitle(null);
            
            // Then
            assertNull(validReview.getTitle());
        }

        @Test
        @DisplayName("Should get and set director correctly")
        void shouldGetAndSetDirectorCorrectly() {
            // Given
            String newDirector = "New Director";
            
            // When
            validReview.setDirector(newDirector);
            
            // Then
            assertEquals(newDirector, validReview.getDirector());
        }

        @Test
        @DisplayName("Should get and set genre correctly")
        void shouldGetAndSetGenreCorrectly() {
            // Given
            String newGenre = "Horror";
            
            // When
            validReview.setGenre(newGenre);
            
            // Then
            assertEquals(newGenre, validReview.getGenre());
        }

        @Test
        @DisplayName("Should get and set review correctly")
        void shouldGetAndSetReviewCorrectly() {
            // Given
            String newReview = "This is an amazing movie!";
            
            // When
            validReview.setReview(newReview);
            
            // Then
            assertEquals(newReview, validReview.getReview());
        }

        @Test
        @DisplayName("Should handle null review")
        void shouldHandleNullReview() {
            // When
            validReview.setReview(null);
            
            // Then
            assertNull(validReview.getReview());
        }

        @Test
        @DisplayName("Should handle empty review")
        void shouldHandleEmptyReview() {
            // When
            validReview.setReview("");
            
            // Then
            assertEquals("", validReview.getReview());
        }

        @Test
        @DisplayName("Should get and set date watched correctly")
        void shouldGetAndSetDateWatchedCorrectly() {
            // Given
            LocalDate newDate = LocalDate.of(2024, 6, 15);
            
            // When
            validReview.setDateWatched(newDate);
            
            // Then
            assertEquals(newDate, validReview.getDateWatched());
        }

        @Test
        @DisplayName("Should handle null date watched")
        void shouldHandleNullDateWatched() {
            // When
            validReview.setDateWatched(null);
            
            // Then
            assertNull(validReview.getDateWatched());
        }
    }

    @Nested
    @DisplayName("Rating Tests")
    class RatingTests {

        @Test
        @DisplayName("Should accept valid rating within range")
        void shouldAcceptValidRatingWithinRange() {
            // Test various valid ratings
            double[] validRatings = {0.0, 0.5, 1.0, 2.5, 3.0, 4.5, 5.0};
            
            for (double rating : validRatings) {
                // When
                validReview.setRating(rating);
                
                // Then
                assertEquals(rating, validReview.getRating(), 0.001);
            }
        }

        @Test
        @DisplayName("Should reject rating below minimum")
        void shouldRejectRatingBelowMinimum() {
            // Given
            double originalRating = validReview.getRating();
            double invalidRating = -0.1;
            
            // When
            validReview.setRating(invalidRating);
            
            // Then
            assertEquals(originalRating, validReview.getRating(), 0.001);
        }

        @Test
        @DisplayName("Should reject rating above maximum")
        void shouldRejectRatingAboveMaximum() {
            // Given
            double originalRating = validReview.getRating();
            double invalidRating = 5.1;
            
            // When
            validReview.setRating(invalidRating);
            
            // Then
            assertEquals(originalRating, validReview.getRating(), 0.001);
        }

        @Test
        @DisplayName("Should handle boundary values correctly")
        void shouldHandleBoundaryValuesCorrectly() {
            // Test exact boundaries
            validReview.setRating(0.0);
            assertEquals(0.0, validReview.getRating(), 0.001);
            
            validReview.setRating(5.0);
            assertEquals(5.0, validReview.getRating(), 0.001);
        }
    }

    @Nested
    @DisplayName("Date String Conversion Tests")
    class DateStringConversionTests {

        @Test
        @DisplayName("Should convert date to string correctly")
        void shouldConvertDateToStringCorrectly() {
            // Given
            LocalDate date = LocalDate.of(2023, 12, 25);
            validReview.setDateWatched(date);
            
            // When
            String dateString = validReview.getDateWatchedAsString();
            
            // Then
            assertEquals("12/25/2023", dateString);
        }

        @Test
        @DisplayName("Should handle single digit month and day in string conversion")
        void shouldHandleSingleDigitMonthAndDayInStringConversion() {
            // Given
            LocalDate date = LocalDate.of(2023, 1, 5);
            validReview.setDateWatched(date);
            
            // When
            String dateString = validReview.getDateWatchedAsString();
            
            // Then
            assertEquals("01/05/2023", dateString);
        }

        @Test
        @DisplayName("Should return empty string for null date")
        void shouldReturnEmptyStringForNullDate() {
            // Given
            validReview.setDateWatched(null);
            
            // When
            String dateString = validReview.getDateWatchedAsString();
            
            // Then
            assertEquals("", dateString);
        }
    }

    @Nested
    @DisplayName("Equals Tests")
    class EqualsTests {

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Then
            assertEquals(validReview, validReview);
        }

        @Test
        @DisplayName("Should be equal to review with same title and director")
        void shouldBeEqualToReviewWithSameTitleAndDirector() {
            // Given
            MovieReview otherReview = new MovieReview(VALID_TITLE, VALID_DIRECTOR, "Different Genre", 2.0, "01/01/2000");
            
            // Then
            assertEquals(validReview, otherReview);
        }

        @Test
        @DisplayName("Should not be equal to review with different title")
        void shouldNotBeEqualToReviewWithDifferentTitle() {
            // Given
            MovieReview otherReview = new MovieReview("Different Title", VALID_DIRECTOR, VALID_GENRE, VALID_RATING, VALID_DATE);
            
            // Then
            assertNotEquals(validReview, otherReview);
        }

        @Test
        @DisplayName("Should not be equal to review with different director")
        void shouldNotBeEqualToReviewWithDifferentDirector() {
            // Given
            MovieReview otherReview = new MovieReview(VALID_TITLE, "Different Director", VALID_GENRE, VALID_RATING, VALID_DATE);
            
            // Then
            assertNotEquals(validReview, otherReview);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Then
            assertNotEquals(null, validReview);
        }

        @Test
        @DisplayName("Should handle case sensitivity in title and director")
        void shouldHandleCaseSensitivityInTitleAndDirector() {
            // Given
            MovieReview otherReview = new MovieReview(VALID_TITLE.toUpperCase(), VALID_DIRECTOR.toLowerCase(), VALID_GENRE, VALID_RATING, VALID_DATE);
            
            // Then
            assertNotEquals(validReview, otherReview);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return correct string format")
        void shouldReturnCorrectStringFormat() {
            // When
            String result = validReview.toString();
            
            // Then
            assertEquals(VALID_TITLE + " - " + VALID_DIRECTOR, result);
        }

        @Test
        @DisplayName("Should handle null title in toString")
        void shouldHandleNullTitleInToString() {
            // Given
            validReview.setTitle(null);
            
            // When
            String result = validReview.toString();
            
            // Then
            assertEquals("null - " + VALID_DIRECTOR, result);
        }

        @Test
        @DisplayName("Should handle null director in toString")
        void shouldHandleNullDirectorInToString() {
            // Given
            validReview.setDirector(null);
            
            // When
            String result = validReview.toString();
            
            // Then
            assertEquals(VALID_TITLE + " - null", result);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            // Given
            String longString = "A".repeat(1000);
            
            // When
            validReview.setTitle(longString);
            validReview.setDirector(longString);
            validReview.setGenre(longString);
            validReview.setReview(longString);
            
            // Then
            assertEquals(longString, validReview.getTitle());
            assertEquals(longString, validReview.getDirector());
            assertEquals(longString, validReview.getGenre());
            assertEquals(longString, validReview.getReview());
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void shouldHandleSpecialCharactersInStrings() {
            // Given
            String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
            
            // When
            validReview.setTitle(specialChars);
            validReview.setDirector(specialChars);
            validReview.setGenre(specialChars);
            validReview.setReview(specialChars);
            
            // Then
            assertEquals(specialChars, validReview.getTitle());
            assertEquals(specialChars, validReview.getDirector());
            assertEquals(specialChars, validReview.getGenre());
            assertEquals(specialChars, validReview.getReview());
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void shouldHandleUnicodeCharacters() {
            // Given
            String unicodeString = "电影评论 🎬 导演";
            
            // When
            validReview.setTitle(unicodeString);
            validReview.setDirector(unicodeString);
            validReview.setGenre(unicodeString);
            validReview.setReview(unicodeString);
            
            // Then
            assertEquals(unicodeString, validReview.getTitle());
            assertEquals(unicodeString, validReview.getDirector());
            assertEquals(unicodeString, validReview.getGenre());
            assertEquals(unicodeString, validReview.getReview());
        }

        @Test
        @DisplayName("Should handle whitespace-only strings")
        void shouldHandleWhitespaceOnlyStrings() {
            // Given
            String whitespaceString = "   \t\n   ";
            
            // When
            validReview.setTitle(whitespaceString);
            validReview.setDirector(whitespaceString);
            validReview.setGenre(whitespaceString);
            validReview.setReview(whitespaceString);
            
            // Then
            assertEquals(whitespaceString, validReview.getTitle());
            assertEquals(whitespaceString, validReview.getDirector());
            assertEquals(whitespaceString, validReview.getGenre());
            assertEquals(whitespaceString, validReview.getReview());
        }
    }
}
