package com.example.eduplanet_e_learningapp.Modals;

public class Message {
    String messageId, message, imageUrl, messagedBy, messageType;
    long messagedAt;

    public Message() {

    }

    public Message(String message, String messagedBy, String messageType, long messagedAt) {
        this.message = message;
        this.messagedBy = messagedBy;
        this.messageType = messageType;
        this.messagedAt = messagedAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessagedBy() {
        return messagedBy;
    }

    public void setMessagedBy(String messagedBy) {
        this.messagedBy = messagedBy;
    }

    public long getMessagedAt() {
        return messagedAt;
    }

    public void setMessagedAt(long messagedAt) {
        this.messagedAt = messagedAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
