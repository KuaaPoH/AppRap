package com.example.rapapp.models;

public class Movie {
    private int id;
    private String title;
    private String description;
    private String poster;
    private int duration;
    private double rating;
    private String ageRating; // Thêm trường này

    public Movie() {}

    public Movie(int id, String title, String description, String poster, int duration, double rating, String ageRating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.poster = poster;
        this.duration = duration;
        this.rating = rating;
        this.ageRating = ageRating;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }
}
