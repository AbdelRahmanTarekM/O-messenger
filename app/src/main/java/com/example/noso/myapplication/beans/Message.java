package com.example.noso.myapplication.beans;

public class Message {
    private String senderId;
    private String senderName;
    private int type;
    private String payload;
    private String conversationId;

    public Message(String senderId, String senderName, int type, String payload, String conversationId) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.type = type;
        this.payload = payload;
        this.conversationId = conversationId;
    }

    public Message() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
