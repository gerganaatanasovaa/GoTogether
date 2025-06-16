package com.example.pmu.models;

public class ConversationModel {
    private User    user;
    private Message lastMessage;

    public ConversationModel(User user, Message lastMessage) {
        this.user        = user;
        this.lastMessage = lastMessage;
    }

    public User getUser() {
        return user;
    }

    public Message getLastMessage() {
        return lastMessage;
    }
}
