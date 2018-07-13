package com.example.rickh.chatapp.models;

import java.util.Date;

public class Message {

    String id;
    String messageText;
    Date time;
    String userId;

    public Message() {

    }

    public Message(String id, String messageText, Date time, String userId) {
        this.id = id;
        this.messageText = messageText;
        this.time = time;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public Message setId(String id) {
        this.id = id;
        return this;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}