package com.cpp.moviejournal.manager;

import com.cpp.moviejournal.model.MovieReview;

public interface ReviewChangeListener {
    void onReviewAdded(MovieReview review);
    void onReviewUpdated(MovieReview review);
    void onReviewDeleted(int reviewId);
    void onReviewsBulkDeleted(int count);
    void onReviewsCleared();
}



