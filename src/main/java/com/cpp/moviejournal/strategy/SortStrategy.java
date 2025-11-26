package com.cpp.moviejournal.strategy;

import com.cpp.moviejournal.model.MovieReview;
import java.util.List;

/**
 * Strategy Pattern: Interface for different sorting strategies
 */
public interface SortStrategy {
    /**
     * Sorts a list of movie reviews according to the specific strategy
     * @param reviews List of reviews to sort
     * @return Sorted list of reviews
     */
    List<MovieReview> sort(List<MovieReview> reviews);
}

