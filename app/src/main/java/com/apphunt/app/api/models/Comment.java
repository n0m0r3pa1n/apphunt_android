package com.apphunt.app.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Comment {
    @SerializedName("createdBy")
    private User user;
    private String parent;
    private ArrayList<Comment> children = new ArrayList<>();
    private ArrayList<Vote> votes = new ArrayList<>();
    private int votesCount;
    private String text;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public ArrayList<Comment> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Comment> children) {
        this.children = children;
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<Vote> votes) {
        this.votes = votes;
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

    @Override
    public String toString() {
        return "Comment{" +
                "user=" + user +
                ", parent=" + parent +
                ", children=" + children +
                ", votes=" + votes +
                ", votesCount=" + votesCount +
                ", text='" + text + '\'' +
                '}';
    }
}
