package com.example.noso.myapplication.beans;

import java.util.List;

public class Conversation {

    private List<Friends> users;
    private List<String> messages;
    private String _id;

    public List<Friends> getUsers() {
        return users;
    }

    public void setUsers(List<Friends> users) {
        this.users = users;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Conversation(List<Friends> users) {
        this.users = users;
    }

    public Conversation() {
    }
}
