package com.example.rapapp.models;

import com.google.firebase.Timestamp;

public class Movie {
    private String id; // Sử dụng String cho Document ID của Firestore
    private String title;
    private String description;
    private String posterUrl;
    private int duration;
    private double rating;
    private String ageRating;
    private Timestamp releaseDate; // Ngày phát hành để sắp xếp

    public Movie() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public Timestamp getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Timestamp releaseDate) { this.releaseDate = releaseDate; }
}
