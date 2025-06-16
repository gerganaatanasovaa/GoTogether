package com.example.pmu.interfaces;

public interface VerificationCodeListener {
    void onSuccess();
    void onFailure(String errorMessage);
}
