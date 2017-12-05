package com.example.luci.roamappdevelopment;

/**
 * Created by LUCI on 11/7/2017.
 */

public class PostInfo
{
     String key;
    public String userEmail;
    public boolean publicState;

    public PostInfo(){}

    public PostInfo(String key, String userEmail, boolean publicState) {
        this.key = key;
        this.userEmail = userEmail;
        this.publicState = publicState;
    }

    @Override
    public String toString() {
        return "PostInfo{" +
                "key='" + key + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", publicState=" + publicState +
                '}';
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setPublicState(boolean publicState) {
        this.publicState = publicState;
    }

    public String getKey() {

        return key;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean isPublicState() {
        return publicState;
    }
}


