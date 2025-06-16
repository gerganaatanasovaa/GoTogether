package com.example.pmu.interfaces;

import com.example.pmu.models.Plan;

public interface PlanInfoListener {
    void onSuccess(Plan plan);
    void onFailure(String message);
}