package com.example.pmu.models;

import com.google.firebase.Timestamp;

public class Photo {
    private String imageUrl;
    private Timestamp timestamp;

    public Photo() {}

    public Photo(String imageUrl, Timestamp timestamp) {
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
