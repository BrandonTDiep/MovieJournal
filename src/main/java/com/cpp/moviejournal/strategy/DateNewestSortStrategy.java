package com.cpp.moviejournal.strategy;

import com.cpp.moviejournal.model.MovieReview;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy Pattern: Sorts reviews by date (newest first)
 */
public class DateNewestSortStrategy implements SortStrategy {
    @Override
    public List<MovieReview> sort(List<MovieReview> reviews) {
        List<MovieReview> sorted = new ArrayList<>(reviews);
        sorted.sort(Comparator
            .comparing(MovieReview::getDateWatched, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(MovieReview::getId, Comparator.reverseOrder()));
        return sorted;
    }
}

