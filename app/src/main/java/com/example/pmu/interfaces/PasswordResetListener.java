package com.example.pmu.interfaces;

public interface PasswordResetListener {
    void onSuccess();
    void onFailure(String errorMessage);
}
