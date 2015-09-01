package com.apphunt.app.api.apphunt.models.apps;

import com.apphunt.app.api.apphunt.models.users.User;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 5/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class BaseApp implements Serializable {
    @SerializedName("_id")
    private String id;
    private String name;
    private String description;
    private String icon;
    private transient int position;
    @SerializedName("package")
    private String packageName;
    private int votesCount;
    @SerializedName("averageScore")
    private double rating;
    private String shortUrl;
    private String url;
    private boolean hasVoted;
    private boolean isFavourite;
    private User createdBy;
    private int commentsCount;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> screenshots = new ArrayList<>();


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

    public boolean hasVoted() {
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

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public ArrayList<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(ArrayList<String> screenshots) {
        this.screenshots = screenshots;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    @Override
    public String toString() {
        return "BaseApp{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", position=" + position +
                ", packageName='" + packageName + '\'' +
                ", votesCount=" + votesCount +
                ", shortUrl='" + shortUrl + '\'' +
                ", url='" + url + '\'' +
                ", hasUserVoted=" + hasVoted +
                ", createdBy=" + createdBy +
                ", commentsCount=" + commentsCount +
                '}';
    }
}
