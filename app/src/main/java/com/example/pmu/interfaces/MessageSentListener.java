package com.example.pmu.interfaces;

public interface MessageSentListener {
    void onSuccess();
    void onFailure(String error);
}
