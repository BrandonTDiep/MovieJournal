package com.cpp.moviejournal.strategy;

/**
 * Factory for creating sort strategies based on sort option string.
 * Implements the Factory Pattern to create appropriate sorting strategies.
 */
public class SortStrategyFactory {
  private static final String SORT_DATE_NEWEST = "Date (Newest)";
  private static final String SORT_DATE_OLDEST = "Date (Oldest)";
  private static final String SORT_RATING_HIGH = "Rating (High)";
  private static final String SORT_RATING_LOW = "Rating (Low)";
  private static final String SORT_TITLE_ASC = "Title (A-Z)";
  private static final String SORT_TITLE_DESC = "Title (Z-A)";

  /**
   * Creates a sort strategy based on the provided sort option.
   *
   * @param sortOption the sort option string (e.g., "Date (Newest)", "Rating (High)")
   * @return the appropriate SortStrategy implementation, defaults to DateNewestSortStrategy
   */
  public static SortStrategy createStrategy(String sortOption) {
    if (sortOption == null) {
      return new DateNewestSortStrategy();
    }

    return switch (sortOption) {
      case SORT_DATE_NEWEST -> new DateNewestSortStrategy();
      case SORT_DATE_OLDEST -> new DateOldestSortStrategy();
      case SORT_RATING_HIGH -> new RatingHighSortStrategy();
      case SORT_RATING_LOW -> new RatingLowSortStrategy();
      case SORT_TITLE_ASC -> new TitleAscendingSortStrategy();
      case SORT_TITLE_DESC -> new TitleDescendingSortStrategy();
      default -> new DateNewestSortStrategy();
    };
  }
}

