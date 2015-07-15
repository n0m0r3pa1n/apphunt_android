package com.apphunt.app.api.apphunt.models.votes;

import com.apphunt.app.api.apphunt.models.users.User;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppVote implements Serializable {
    private String userId;
    private String appId;
    @SerializedName("votesCount")
    private String votes;
    private User user;

    public AppVote(String userId) {
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
}
