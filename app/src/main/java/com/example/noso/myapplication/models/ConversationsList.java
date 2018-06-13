package com.example.noso.myapplication.models;

import java.util.ArrayList;
import java.util.List;

public class ConversationsList {
    private List<Conversation> conversations;

    public ConversationsList(List<Conversation> conversations) {

    }

    public ConversationsList() {
        this.conversations = new ArrayList<>();
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}
