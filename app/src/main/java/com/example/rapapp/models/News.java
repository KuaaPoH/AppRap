package com.example.rapapp.models;

import com.google.firebase.Timestamp;

public class News {
    private String id;
    private String title;
    private String imageUrl;
    private String category; // "Review", "News", "Character"
    private java.util.List<ContentBlock> contentBlocks;
    private Timestamp publishedDate;

    public News() {
        // Required for Firebase
    }

    public News(String title, String imageUrl, String category, java.util.List<ContentBlock> contentBlocks, Timestamp publishedDate) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.category = category;
        this.contentBlocks = contentBlocks;
        this.publishedDate = publishedDate;
    }

    public static class ContentBlock {
        private String type; // "text" or "image"
        private String value;

        public ContentBlock() {}

        public ContentBlock(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
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

    public java.util.List<ContentBlock> getContentBlocks() {
        return contentBlocks;
    }

    public void setContentBlocks(java.util.List<ContentBlock> contentBlocks) {
        this.contentBlocks = contentBlocks;
    }

    public Timestamp getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Timestamp publishedDate) {
        this.publishedDate = publishedDate;
    }

    // Helper method for backward compatibility in Adapter if needed
    public String getContentSummary() {
        if (contentBlocks != null) {
            for (ContentBlock block : contentBlocks) {
                if ("text".equals(block.getType())) return block.getValue();
            }
        }
        return "";
    }
}
