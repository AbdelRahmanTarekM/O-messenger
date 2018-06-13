package com.example.noso.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.noso.myapplication.models.Conversation;
import com.example.noso.myapplication.models.ConversationsList;
import com.google.gson.Gson;

import java.util.List;


/**
 * Created by NOSO on 11/29/2017.
 */

public class PreferenceManager extends AppCompatActivity {

    public static final String KEY_NAME = "Username";
    public static final String KEY_PASSWORD = "Password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID = "id";
    public static final String KEY_X_AUTH = "x-auth";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String KEY_CONVERSATION = "conversations";
    private String TAG = "Homie";
    public static ConversationsList conversations;
    public static String xAuthToken, email, id, username;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    Integer mode = 0;

    public PreferenceManager() {
    }

    public PreferenceManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("myPref", mode);
        editor = pref.edit();
    }

    public void LoginSession(String email, String xAuth, String userName, String Id) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, userName);
        editor.putString(KEY_X_AUTH, xAuth);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ID, Id);
        xAuthToken = xAuth;
        PreferenceManager.email = email;
        username = userName;
        id = Id;
        Log.e(TAG, "LoginSession: username: " + userName);
        Log.e(TAG, "LoginSession: email: " + email);
        Log.e(TAG, "LoginSession: id: " + id);
        editor.commit();
    }

    public String returnxAuth() {
        return xAuthToken;
    }


    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);

        }
    }

    public boolean isLoggedIn() {
        xAuthToken = pref.getString(KEY_X_AUTH, null);
        loadConversations();
        username = pref.getString(KEY_EMAIL, null);
        id = pref.getString(KEY_ID, null);
        email = pref.getString(KEY_NAME, null);
        Log.e(TAG, "LoginSession: username: " + username);
        Log.e(TAG, "LoginSession: email: " + email);
        Log.e(TAG, "LoginSession: id: " + id);
        Log.d("O-messenger", "X-auth token: " + xAuthToken);
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void addConversation(Conversation conversation) {
        conversations.getConversations().add(conversation);
        Gson gson = new Gson();
        String temp = gson.toJson(conversations, ConversationsList.class);
        editor.putString(KEY_CONVERSATION, temp);
        editor.commit();
    }

    private void loadConversations() {
        Gson gson = new Gson();
        String temp = pref.getString(KEY_CONVERSATION, null);
        if (temp == null) {
            conversations = new ConversationsList();
        } else {
            conversations = gson.fromJson(temp, ConversationsList.class);
        }

    }

    public static void setConversations(List<Conversation> conversations) {
        PreferenceManager.conversations.getConversations().clear();
        PreferenceManager.conversations.getConversations().addAll(conversations);

    }
}

