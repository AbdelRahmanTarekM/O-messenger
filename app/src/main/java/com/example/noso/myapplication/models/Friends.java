package com.example.noso.myapplication.models;

/**
 * Created by omnia on 2/10/2018.
 */

public class Friends {
    private String username;
    private String _id;
    private String email;
    private String userId;

    public Friends(String userName, String ID, String userId,String email) {
        this.username = userName;
        this._id = ID;
        this.userId = userId;
        this.email = email;
    }

    public Friends(String username, String email, String userId) {
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getID() {
        return userId;
    }

    public void setID(String ID) {
        this._id = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
