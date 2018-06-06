package com.example.noso.myapplication.Interfaces;

import com.example.noso.myapplication.beans.Conversation;
import com.example.noso.myapplication.beans.Friends;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ConversationsClient {

    @POST("/conversations/newConversation")
    Call<Conversation> newConversation(@Body Conversation users);
}
