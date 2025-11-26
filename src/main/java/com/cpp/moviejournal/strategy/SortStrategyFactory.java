package com.cpp.moviejournal.strategy;

/**
 * Factory for creating sort strategies based on sort option string
 */
public class SortStrategyFactory {
    /**
     * Creates a sort strategy based on the provided sort option
     * @param sortOption The sort option string (e.g., "Date (Newest)", "Rating (High)")
     * @return The appropriate SortStrategy implementation
     */
    public static SortStrategy createStrategy(String sortOption) {
        if (sortOption == null) {
            return new DateNewestSortStrategy(); // Default
        }
        
        return switch (sortOption) {
            case "Date (Newest)" -> new DateNewestSortStrategy();
            case "Date (Oldest)" -> new DateOldestSortStrategy();
            case "Rating (High)" -> new RatingHighSortStrategy();
            case "Rating (Low)" -> new RatingLowSortStrategy();
            case "Title (A-Z)" -> new TitleAscendingSortStrategy();
            case "Title (Z-A)" -> new TitleDescendingSortStrategy();
            default -> new DateNewestSortStrategy(); // Default fallback
        };
    }
}

