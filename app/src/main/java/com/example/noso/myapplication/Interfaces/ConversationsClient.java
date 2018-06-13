package com.example.noso.myapplication.Interfaces;

import com.example.noso.myapplication.models.Conversation;
import com.example.noso.myapplication.models.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConversationsClient {

    @POST("/conversations/newConversation")
    Call<Conversation> newConversation(@Body Conversation users);

    @GET("/conversations/userChats/{id}")
    Call<List<Conversation>> getConversations(@Path("id") String id);

    @GET("/conversations/chatMessages/{id}")
    Call<List<Message>> getMessages(@Path("id") String id);
}
