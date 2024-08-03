package com.example.purrpalsapp.ui.chat;

public class MessageModal {
    private String senderEmail;
    private boolean senderType;
    private String text;
    private long time;
    private String userId;

    public MessageModal() {
        // Default constructor required for Firebase
    }

    public MessageModal(String senderName, boolean senderType, String text, long time, String userId) {
        this.senderEmail = senderName;
        this.senderType = senderType;
        this.text = text;
        this.time = time;
        this.userId = userId;
    }

    // Getters and setters for each field

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public boolean isSenderType() {
        return senderType;
    }

    public void setSenderType(boolean senderType) {
        this.senderType = senderType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
