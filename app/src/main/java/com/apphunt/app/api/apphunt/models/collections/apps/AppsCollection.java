package com.apphunt.app.api.apphunt.models.collections.apps;

import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.users.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 5/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class AppsCollection {

    @SerializedName("_id")
    private String id;
    private String picture;
    private String name;
    private String description;
    private boolean hasVoted;
    private boolean isFavourite;
    private int votesCount;

    private User createdBy;

    private List<BaseApp> apps = new ArrayList<>();
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public boolean hasUserVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public int getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    public List<BaseApp> getApps() {
        return apps;
    }

    public void setApps(List<BaseApp> apps) {
        this.apps = apps;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    @Override
    public String toString() {
        return "AppsCollection{" +
                "id='" + id + '\'' +
                ", picture='" + picture + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", apps=" + apps +
                '}';
    }
}
