package com.example.pmu.interfaces;

public interface ImageUploadListener {
    void onSuccess(String imageUrl);
    void onFailure(String errorMessage);
}
