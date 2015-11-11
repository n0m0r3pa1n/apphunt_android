package com.apphunt.app.api.apphunt.models.comments;

import com.apphunt.app.api.apphunt.models.users.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Comment {
    @SerializedName("_id")
    private String id;
    @SerializedName("createdBy")
    private User user;
    private String userId;
    private String appId;
    private String parent;
    private ArrayList<Comment> children = new ArrayList<>();
    private int votesCount;
    private String text;
    private boolean hasVoted;
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Comment> getChildren() {
        return children;
    }

    public int getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", userId='" + userId + '\'' +
                ", appId='" + appId + '\'' +
                ", parent='" + parent + '\'' +
                ", children=" + children +
                ", votesCount=" + votesCount +
                ", text='" + text + '\'' +
                ", hasVoted=" + hasVoted +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
