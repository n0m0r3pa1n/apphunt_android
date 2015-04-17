package com.apphunt.app.api.apphunt.models;

import com.google.gson.annotations.SerializedName;

public class Vote {
    private String userId;
    private String appId;
    @SerializedName("votesCount")
    private String votes;
    private User user;

    public Vote(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "userId='" + userId + '\'' +
                ", appId='" + appId + '\'' +
                ", votes='" + votes + '\'' +
                ", user=" + user.toString() +
                '}';
    }
}
