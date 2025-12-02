package com.cpp.moviejournal.strategy;

import com.cpp.moviejournal.model.MovieReview;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy Pattern: Sorts reviews by title (Z-A).
 * Reviews with the same title are sorted by director in descending order.
 */
public class TitleDescendingSortStrategy implements SortStrategy {
  @Override
  public List<MovieReview> sort(List<MovieReview> reviews) {
    List<MovieReview> sorted = new ArrayList<>(reviews);
    sorted.sort(
        Comparator.comparing(
                MovieReview::getTitle,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()))
            .thenComparing(MovieReview::getDirector, Comparator.reverseOrder()));
    return sorted;
  }
}

