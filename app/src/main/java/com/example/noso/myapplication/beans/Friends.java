package com.example.noso.myapplication.beans;

/**
 * Created by omnia on 2/10/2018.
 */

public class Friends {
    private String username;
    private String _id;
    private String userId;
    private String picURL;

    public Friends(String userName, String ID, String userId) {
        this.username = userName;
        this._id = ID;
        this.userId = userId;

    }
    public Friends( String userName, String ID,String userId, String picUrl) {
        this.username = userName;
        this._id = ID;
        this.userId = userId;
        this.picURL = picUrl;
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
    public String getPicURL() {
        return picURL;
    }



}
