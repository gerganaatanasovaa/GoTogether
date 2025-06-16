package com.example.pmu.interfaces;

import com.example.pmu.models.User;

public interface UserFetchListener {
    void onSuccess(User user);
    void onFailure(String message);
}
