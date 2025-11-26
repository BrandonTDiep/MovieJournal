package com.cpp.moviejournal.strategy;

import com.cpp.moviejournal.model.MovieReview;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy Pattern: Sorts reviews by rating (lowest first)
 */
public class RatingLowSortStrategy implements SortStrategy {
    @Override
    public List<MovieReview> sort(List<MovieReview> reviews) {
        List<MovieReview> sorted = new ArrayList<>(reviews);
        sorted.sort(Comparator
            .comparing(MovieReview::getRating)
            .thenComparing(MovieReview::getTitle));
        return sorted;
    }
}

