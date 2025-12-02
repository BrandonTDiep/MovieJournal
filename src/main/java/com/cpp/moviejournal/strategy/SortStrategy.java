package com.cpp.moviejournal.strategy;

import com.cpp.moviejournal.model.MovieReview;
import java.util.List;

/**
 * Strategy Pattern: Interface for different sorting strategies.
 * Defines the contract for sorting movie reviews.
 */
public interface SortStrategy {
  /**
   * Sorts a list of movie reviews according to the specific strategy.
   *
   * @param reviews list of reviews to sort
   * @return sorted list of reviews
   */
  List<MovieReview> sort(List<MovieReview> reviews);
}

