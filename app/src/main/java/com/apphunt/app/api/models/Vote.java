package com.apphunt.app.api.models;

import com.google.gson.annotations.SerializedName;

public class Vote {
    private String userId;
    private String appId;
    @SerializedName("votesCount")
    private String votes;

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
}
