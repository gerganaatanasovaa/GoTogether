package com.example.pmu.interfaces;

import com.example.pmu.models.Destination;

public interface DestinationClickListener {
    void onSuccess(Destination destination);
    void onFailure(String message);
}
