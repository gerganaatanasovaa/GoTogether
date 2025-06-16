package com.example.pmu.models;

import com.google.firebase.Timestamp;

public class ChatPreview {
    private String chatId;
    private String lastMessage;
    private String senderId;
    private String receiverId;
    private Timestamp timestamp;

    public ChatPreview() {}

    public ChatPreview(String chatId, String lastMessage, String senderId, String receiverId, Timestamp timestamp) {
        this.chatId = chatId;
        this.lastMessage = lastMessage;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
    }

    public String getChatId() {
        return chatId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
