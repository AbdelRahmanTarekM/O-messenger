package com.example.noso.myapplication.models;

import com.example.noso.myapplication.models.Friends;

/**
 * Created by omnia on 2/10/2018.
 */

public class Users {
    Friends[] friends;
    private String _id;
    private String username;
    private String email;
    private String password;
    private String fcm_token;
    private String displayPicture;

    public Users(String username, String email, String password,String displayPicture) {

        this.username = username;
        this.email = email;
        this.password = password;
        this.displayPicture = displayPicture;
        fcm_token = "123ABC";

    }

    public Users(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Users() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Friends[] getFriends() {
        return friends;
    }

    public void setFriends(Friends[] friends) {
        this.friends = friends;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }
}
