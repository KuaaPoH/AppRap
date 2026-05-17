package com.example.rapapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import java.util.List;

public class Movie {
    @Exclude
    private String id; // Document ID từ Firestore
    private String title;
    private String description;
    private String posterUrl;
    private int duration;
    private double rating;
    private String ageRating;
    private Timestamp releaseDate;
    private String trailerUrl;  // Link Youtube
    private String director;    // Đạo diễn
    private String cast;        // Diễn viên
    private double price;       // Giá vé
    private List<String> galleryUrls; // Thư viện ảnh

    public Movie() {}

    @Exclude
    public String getId() { return id; }
    @Exclude
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

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public List<String> getGalleryUrls() { return galleryUrls; }
    public void setGalleryUrls(List<String> galleryUrls) { this.galleryUrls = galleryUrls; }
}
