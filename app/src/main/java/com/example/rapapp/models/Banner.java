package com.example.rapapp.models;

public class Banner {
    private String imageUrl;
    private String newsId; // ID của bài báo liên quan

    public Banner() {}

    public Banner(String imageUrl, String newsId) {
        this.imageUrl = imageUrl;
        this.newsId = newsId;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getNewsId() { return newsId; }
    public void setNewsId(String newsId) { this.newsId = newsId; }
}
