package com.example.pmu.interfaces;

import com.example.pmu.models.Message;

import java.util.List;

public interface MessageListener {
    void onSuccess(List<Message> textMessage);
    void onFailure(String message);
}
