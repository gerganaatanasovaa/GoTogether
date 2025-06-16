package com.example.pmu.interfaces;

public interface UserJoinStatusListener {
    void onResult(boolean isJoined);
    void onFailure(String message);
}
