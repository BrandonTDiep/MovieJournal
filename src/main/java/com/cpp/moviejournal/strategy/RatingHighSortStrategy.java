package com.cpp.moviejournal.strategy;

import com.cpp.moviejournal.model.MovieReview;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy Pattern: Sorts reviews by rating (highest first)
 */
public class RatingHighSortStrategy implements SortStrategy {
    @Override
    public List<MovieReview> sort(List<MovieReview> reviews) {
        List<MovieReview> sorted = new ArrayList<>(reviews);
        sorted.sort(Comparator
            .comparing(MovieReview::getRating, Comparator.reverseOrder())
            .thenComparing(MovieReview::getTitle));
        return sorted;
    }
}

