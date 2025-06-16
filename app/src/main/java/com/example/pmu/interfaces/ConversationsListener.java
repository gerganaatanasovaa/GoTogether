package com.example.pmu.interfaces;

import com.example.pmu.models.ConversationModel;

import java.util.List;

public interface ConversationsListener {
    void onSuccess(List<ConversationModel> conversations);
    void onFailure(String errorMessage);
}
