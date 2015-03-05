package com.apphunt.app.api.models;

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

    public String getParentId() {
        return parent;
    }

    public void setParentId(String parent) {
        this.parent = parent;
    }

    public ArrayList<Comment> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Comment> children) {
        this.children = children;
    }
    
    public void setChild(Comment comment) {
        this.children.add(comment);
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
                '}';
    }
}
