package com.cpp.moviejournal.manager;

import com.cpp.moviejournal.model.MovieReview;

/**
 * Observer Pattern: Interface for listeners that respond to review changes.
 * Implementations are notified when reviews are added, updated, or deleted.
 */
public interface ReviewChangeListener {
  /**
   * Called when a review is added.
   *
   * @param review the newly added review
   */
  void onReviewAdded(MovieReview review);

  /**
   * Called when a review is updated.
   *
   * @param review the updated review
   */
  void onReviewUpdated(MovieReview review);

  /**
   * Called when a review is deleted.
   *
   * @param reviewId the ID of the deleted review
   */
  void onReviewDeleted(int reviewId);

  /**
   * Called when multiple reviews are deleted in bulk.
   *
   * @param count the number of reviews deleted
   */
  void onReviewsBulkDeleted(int count);

  /**
   * Called when all reviews are cleared.
   */
  void onReviewsCleared();
}



