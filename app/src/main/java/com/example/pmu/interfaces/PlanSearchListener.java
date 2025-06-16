package com.example.pmu.interfaces;

import com.example.pmu.models.Plan;

import java.util.List;

public interface PlanSearchListener {
    void onSuccess(List<Plan> plans);
    void onFailure(String error);
}

