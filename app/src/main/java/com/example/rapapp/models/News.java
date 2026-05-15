package com.example.rapapp.models;

import com.google.firebase.Timestamp;

public class News {
    private String id;
    private String title;
    private String imageUrl;
    private String category; // "Review", "News", "Character"
    private String content;
    private Timestamp publishedDate;

    public News() {
        // Required for Firebase
    }

    public News(String title, String imageUrl, String category, String content, Timestamp publishedDate) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.category = category;
        this.content = content;
        this.publishedDate = publishedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Timestamp publishedDate) {
        this.publishedDate = publishedDate;
    }
}
