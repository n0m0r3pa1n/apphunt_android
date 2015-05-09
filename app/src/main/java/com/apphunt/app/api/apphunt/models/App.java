package com.apphunt.app.api.apphunt.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class App {
    @SerializedName("_id")
    private String id;
    private String name;
    private String description;
    private String icon;
    private transient int position;
    @SerializedName("package")
    private String packageName;
    private int votesCount;
    private String shortUrl;
    private String url;
    private boolean hasVoted;
    private ArrayList<Vote> votes = new ArrayList<>();
    private User createdBy;
    private int commentsCount;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVotesCount() {
        return String.valueOf(votesCount);
    }

    public void setVotesCount(String votesCount) {
        this.votesCount = Integer.valueOf(votesCount);
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<Vote> votes) {
        this.votes = votes;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    @Override
    public String toString() {
        return "App{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", packageName='" + packageName + '\'' +
                ", votesCount=" + votesCount +
                ", shortUrl='" + shortUrl + '\'' +
                ", url='" + url + '\'' +
                ", hasVoted=" + hasVoted +
                ", votes=" + votes +
                ", createdBy=" + createdBy +
                ", commentsCount=" + commentsCount +
                '}';
    }
}
