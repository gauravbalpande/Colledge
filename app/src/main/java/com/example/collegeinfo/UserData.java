package com.example.collegeinfo;

import com.google.firebase.Timestamp;

public class UserData {
    private String title;
    private String imageUrl;
    private String thoughts;
    private String userId;
    private String userEmail;

    public UserData() {
    }

    public UserData(String title, String imageUrl, String thoughts, String userId, String userEmail) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.thoughts = thoughts;
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
