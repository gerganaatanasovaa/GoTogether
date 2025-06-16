package com.example.pmu.interfaces;

public interface CodeVerificationListener {
    void onSuccess();
    /** Called if there was an error or the code didn’t match. */
    void onFailure(String errorMessage);
}
