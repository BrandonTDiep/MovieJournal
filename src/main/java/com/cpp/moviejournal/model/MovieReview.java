package com.cpp.moviejournal.model;

import java.time.LocalDate;

/**
 * Represents a movie review in the Movie Journal application.
 * Contains information about a movie, user rating, review text, and metadata.
 */
public class MovieReview {
  private static final double MIN_RATING = 0.0;
  private static final double MAX_RATING = 5.0;
  private static final String DATE_SEPARATOR = "/";
  private static final int DATE_PARTS_COUNT = 3;
  private static final int MONTH_INDEX = 0;
  private static final int DAY_INDEX = 1;
  private static final int YEAR_INDEX = 2;
  private static final String EMPTY_STRING = "";
  private static final String DATE_FORMAT = "%02d/%02d/%04d";

  private int id;
  private int userId; // Foreign key to users table
  private String title;
  private String director;
  private String genre;
  private double rating; // 1-5 stars
  private String review;
  private LocalDate dateWatched;
  private String ticketImagePath;
  private boolean favorite;

  /**
   * Constructor for new movie review.
   *
   * @param userId the user ID
   * @param title the movie title
   * @param director the director name
   * @param genre the movie genre
   * @param rating the rating (0-5)
   * @param dateWatched the date watched as string (MM/dd/yyyy)
   */
  public MovieReview(
      int userId, String title, String director, String genre, double rating, String dateWatched) {
    this.userId = userId;
    this.title = title;
    this.director = director;
    this.genre = genre;
    this.rating = rating;
    this.review = EMPTY_STRING;
    this.dateWatched = parseDateString(dateWatched);
  }

  /**
   * Constructor for database retrieval.
   *
   * @param id the review ID
   * @param userId the user ID
   * @param title the movie title
   * @param director the director name
   * @param genre the movie genre
   * @param rating the rating
   * @param review the review text
   * @param dateWatched the date watched
   */
  public MovieReview(
      int id,
      int userId,
      String title,
      String director,
      String genre,
      double rating,
      String review,
      LocalDate dateWatched) {
    this(id, userId, title, director, genre, rating, review, dateWatched, null, false);
  }

  /**
   * Constructor for new movie review without user ID.
   *
   * @param title the movie title
   * @param director the director name
   * @param genre the movie genre
   * @param rating the rating
   * @param dateWatched the date watched as string
   */
  public MovieReview(String title, String director, String genre, double rating, String dateWatched) {
    this(0, title, director, genre, rating, dateWatched);
  }

  /**
   * Full constructor for database retrieval with all fields.
   *
   * @param id the review ID
   * @param userId the user ID
   * @param title the movie title
   * @param director the director name
   * @param genre the movie genre
   * @param rating the rating
   * @param review the review text
   * @param dateWatched the date watched
   * @param ticketImagePath the ticket image path
   * @param favorite whether this is a favorite review
   */
  public MovieReview(
      int id,
      int userId,
      String title,
      String director,
      String genre,
      double rating,
      String review,
      LocalDate dateWatched,
      String ticketImagePath,
      boolean favorite) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.director = director;
    this.genre = genre;
    this.rating = rating;
    this.review = review != null ? review : EMPTY_STRING;
    this.dateWatched = dateWatched;
    this.ticketImagePath = ticketImagePath;
    this.favorite = favorite;
  }

  private LocalDate parseDateString(String dateWatched) {
    if (dateWatched == null || dateWatched.trim().isEmpty()) {
      return LocalDate.now();
    }
    try {
      String[] parts = dateWatched.split(DATE_SEPARATOR);
      if (parts.length == DATE_PARTS_COUNT) {
        int month = Integer.parseInt(parts[MONTH_INDEX]);
        int day = Integer.parseInt(parts[DAY_INDEX]);
        int year = Integer.parseInt(parts[YEAR_INDEX]);
        return LocalDate.of(year, month, day);
      }
    } catch (Exception e) {
      // Fall through to default
    }
    return LocalDate.now();
  }

  // Builder pattern for MovieReview
  /** Builder class for constructing MovieReview objects. */
  public static class Builder {
    private int id;
    private int userId;
    private String title;
    private String director;
    private String genre;
    private double rating;
    private String review;
    private LocalDate dateWatched;
    private String ticketImagePath;
    private boolean favorite;

    public Builder setId(int id) {
      this.id = id;
      return this;
    }

    public Builder setUserId(int userId) {
      this.userId = userId;
      return this;
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder setDirector(String director) {
      this.director = director;
      return this;
    }

    public Builder setGenre(String genre) {
      this.genre = genre;
      return this;
    }

    public Builder setRating(double rating) {
      this.rating = rating;
      return this;
    }

    public Builder setReview(String review) {
      this.review = review;
      return this;
    }

    public Builder setDateWatched(LocalDate dateWatched) {
      this.dateWatched = dateWatched;
      return this;
    }

    public Builder setTicketImagePath(String ticketImagePath) {
      this.ticketImagePath = ticketImagePath;
      return this;
    }

    public Builder setFavorite(boolean favorite) {
      this.favorite = favorite;
      return this;
    }

    /**
     * Builds a MovieReview object from the builder.
     *
     * @return the constructed MovieReview object
     */
    public MovieReview build() {
      LocalDate date = dateWatched != null ? dateWatched : LocalDate.now();
      return new MovieReview(
          id, userId, title, director, genre, rating, review, date, ticketImagePath, favorite);
    }
  }

  /**
   * Creates a new Builder instance for constructing MovieReview objects.
   *
   * @return a new Builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  // Getters and Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getReview() {
    return review;
  }

  public void setReview(String review) {
    this.review = review;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public double getRating() {
    return rating;
  }

  /**
   * Sets the rating, only if it's within valid range (0-5).
   *
   * @param rating the rating value
   */
  public void setRating(double rating) {
    if (isValidRating(rating)) {
      this.rating = rating;
    }
  }

  private boolean isValidRating(double rating) {
    return rating >= MIN_RATING && rating <= MAX_RATING;
  }

  public LocalDate getDateWatched() {
    return dateWatched;
  }

  public void setDateWatched(LocalDate dateWatched) {
    this.dateWatched = dateWatched;
  }

  public String getTicketImagePath() {
    return ticketImagePath;
  }

  public void setTicketImagePath(String ticketImagePath) {
    this.ticketImagePath = ticketImagePath;
  }

  public boolean isFavorite() {
    return favorite;
  }

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  /**
   * Gets the date watched as a formatted string (MM/dd/yyyy).
   *
   * @return the formatted date string, or empty string if date is null
   */
  public String getDateWatchedAsString() {
    if (dateWatched == null) {
      return EMPTY_STRING;
    }
    return String.format(
        DATE_FORMAT,
        dateWatched.getMonthValue(),
        dateWatched.getDayOfMonth(),
        dateWatched.getYear());
  }
    
  @Override
  public String toString() {
    return title + " - " + director;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MovieReview that = (MovieReview) obj;
    return id == that.id
        && userId == that.userId
        && java.util.Objects.equals(title, that.title)
        && java.util.Objects.equals(director, that.director);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(id, userId, title, director);
  }
}