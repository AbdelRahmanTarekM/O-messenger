package com.example.noso.myapplication.models;

import java.util.List;

public class Conversation {

    private List<Friends> users;
    private List<Message> messages;
    private String _id;

    public List<Friends> getUsers() {
        return users;
    }

    public void setUsers(List<Friends> users) {
        this.users = users;
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
