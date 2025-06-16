package com.example.pmu.models;

import com.google.firebase.Timestamp;
import com.google.firebase.database.Exclude;

public class CommentModel {
    private Timestamp createdAt;
    private String imageUrl;
    private String text;
    private String userId;
    @Exclude
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public CommentModel() {}

    public CommentModel(Timestamp createdAt, String imageUrl, String text, String userId) {
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
        this.text = text;
        this.userId = userId;
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
