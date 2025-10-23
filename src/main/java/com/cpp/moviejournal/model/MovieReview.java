package com.cpp.moviejournal.model;

import java.time.LocalDate;

public class MovieReview {
    private int id;
    private int userId; // Foreign key to users table
    private String title;
    private String director;
    private String genre;
    private double rating; // 1-5 stars
    private String review;
    private LocalDate dateWatched;

    // Constructor for new movie review
    public MovieReview(int userId, String title, String director, String genre, double rating, String dateWatched) {
        this.userId = userId;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.rating = rating;
        this.review = "";
        // Parse the date string to LocalDate
        try {
            if (dateWatched != null && !dateWatched.trim().isEmpty()) {
                String[] parts = dateWatched.split("/");
                if (parts.length == 3) {
                    int month = Integer.parseInt(parts[0]);
                    int day = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);
                    this.dateWatched = LocalDate.of(year, month, day);
                } else {
                    this.dateWatched = LocalDate.now();
                }
            } else {
                this.dateWatched = LocalDate.now();
            }
        } catch (Exception e) {
            this.dateWatched = LocalDate.now();
        }
    }

    // Constructor for database retrieval
    public MovieReview(int id, int userId, String title, String director, String genre, double rating, String review, LocalDate dateWatched) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.rating = rating;
        this.review = review != null ? review : "";
        this.dateWatched = dateWatched;
    }

    // Legacy constructor for backward compatibility
    public MovieReview(String title, String director, String genre, double rating, String dateWatched) {
        this(0, title, director, genre, rating, dateWatched); // userId = 0 for legacy usage
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

    public void setRating(double rating) {
        if (rating >= 0 && rating <= 5) {
            this.rating = rating;
        }
    }
    
    public LocalDate getDateWatched() {
        return dateWatched;
    }

    public void setDateWatched(LocalDate dateWatched) {
        this.dateWatched = dateWatched;
    }
    
    // Helper method to get date as string for display
    public String getDateWatchedAsString() {
        if (dateWatched == null) {
            return "";
        }
        return String.format("%02d/%02d/%04d", 
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
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MovieReview that = (MovieReview) obj;
        return id == that.id && userId == that.userId &&
               java.util.Objects.equals(title, that.title) && 
               java.util.Objects.equals(director, that.director);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, userId, title, director);
    }
}
