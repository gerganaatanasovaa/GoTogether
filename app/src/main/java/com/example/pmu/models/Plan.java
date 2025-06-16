package com.example.pmu.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;
    private String destination;
    private String description;
    private String imageUrl;
    private String date;
    private String title;
    private String tripId;
    private String userId;
    private List<String> participants;

    public Plan() {}

    public Plan(String destination, String description, String imageUrl, String date,
                String title, String tripId, String userId,
                List<String> participants) {
        this.destination=destination;
        this.description = description;
        this.imageUrl = imageUrl;
        this.date = date;
        this.title = title;
        this.tripId = tripId;
        this.userId = userId;
        this.participants = participants;
    }

    public String getDestination() { return destination; }
    public void setDestination(String country) { this.destination = country; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }

}
