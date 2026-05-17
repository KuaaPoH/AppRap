package com.example.rapapp.models;

import java.util.List;

public class Banner {
    private String id;
    private String imageUrl;
    private String newsId;
    private List<ContentBlock> contentBlocks;

    public static class ContentBlock {
        private String type; // "text", "image", "title", "bullet"
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

    public Banner() {}

    public Banner(String imageUrl, String newsId) {
        this.imageUrl = imageUrl;
        this.newsId = newsId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getNewsId() { return newsId; }
    public void setNewsId(String newsId) { this.newsId = newsId; }

    public List<ContentBlock> getContentBlocks() { return contentBlocks; }
    public void setContentBlocks(List<ContentBlock> contentBlocks) { this.contentBlocks = contentBlocks; }
}
