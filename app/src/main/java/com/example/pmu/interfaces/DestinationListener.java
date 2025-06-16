package com.example.pmu.interfaces;

import com.example.pmu.models.Destination;

import java.util.List;

public interface DestinationListener {
    void onSuccess(List<Destination> destinations);
    void onFailure(String errorMessage);
}
