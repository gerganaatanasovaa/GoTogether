package com.example.pmu.models;


import android.graphics.Bitmap;

import com.example.pmu.utils.AppService;
import com.google.firebase.Timestamp;

import java.util.BitSet;

public class User {

    private String userId;
    private static User instance = null;
    private String email;
    private String firstName;
    private String country;
    private String bio;
    private String profileImageUrl;
    private com.google.firebase.Timestamp createdAt;

    public User() {

    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public User(String id, String userId,
                String email, String firstName, String country,
                String bio, String profileImageUrl,
                 com.google.firebase.Timestamp createdAt) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.country = country;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
    }

    public static void setInstance(User instance) {
        User.instance = instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    public com.google.firebase.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
