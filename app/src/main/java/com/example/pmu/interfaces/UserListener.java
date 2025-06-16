package com.example.pmu.interfaces;

import com.example.pmu.models.User;

import java.util.List;

public interface UserListener {
    void onSuccess(List<User> users);
    void onFailure(String errorMessage);
}