package com.cpp.moviejournal.strategy;

import com.cpp.moviejournal.model.MovieReview;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy Pattern: Sorts reviews by date (oldest first).
 * Reviews with the same date are sorted by ID in ascending order.
 */
public class DateOldestSortStrategy implements SortStrategy {
  @Override
  public List<MovieReview> sort(List<MovieReview> reviews) {
    List<MovieReview> sorted = new ArrayList<>(reviews);
    sorted.sort(
        Comparator.comparing(
                MovieReview::getDateWatched, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(MovieReview::getId));
    return sorted;
  }
}

