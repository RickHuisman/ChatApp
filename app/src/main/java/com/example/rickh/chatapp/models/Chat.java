package com.example.rickh.chatapp.models;

import java.io.Serializable;
import java.util.HashMap;

public class Chat implements Serializable {

    String id;
    String title;
    HashMap<String, String> members;
    String lastMessage;
    String lastMessageTime;
    String chatIconUrl;

    public Chat() {

    }

    public Chat(String id, String title, HashMap<String, String> members, String lastMessage, String lastMessageTime, String chatIconUrl) {
        this.id = id;
        this.title = title;
        this.members = members;
        this.lastMessage = lastMessage;
    }

    public String getId() {
        return id;
    }

    public Chat setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, String> members) {
        this.members = members;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getChatIconUrl() {
        return chatIconUrl;
    }

    public void setChatIconUrl(String chatIconUrl) {
        this.chatIconUrl = chatIconUrl;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
