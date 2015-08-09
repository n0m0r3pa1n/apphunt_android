package com.apphunt.app.api.apphunt.models.collections.apps;

import android.app.Activity;

import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants.CollectionStatus;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 5/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class AppsCollection implements Serializable {

    @SerializedName("_id")
    private String id;
    private String picture;
    private String name;
    private String description;
    @SerializedName("status")
    private CollectionStatus status;
    private boolean hasVoted;
    private boolean isFavourite;
    private int votesCount;
    private User createdBy;
    private ArrayList<String> tags = new ArrayList<>();

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

    public CollectionStatus getStatus() {
        return status;
    }

    public void setStatus(CollectionStatus status) {
        this.status = status;
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

    public boolean isHasVoted() {
        return hasVoted;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public boolean isOwnedByCurrentUser(Activity context) {
        if(!LoginProviderFactory.get(context).isUserLoggedIn())
            return false;

        return this.createdBy.getId().equals(LoginProviderFactory.get(context).getUser().getId());
    }

    @Override
    public String toString() {
        return "AppsCollection{" +
                "id='" + id + '\'' +
                ", picture='" + picture + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", hasVoted=" + hasVoted +
                ", isFavourite=" + isFavourite +
                ", votesCount=" + votesCount +
                ", createdBy=" + createdBy +
                ", tags=" + tags +
                ", apps=" + apps +
                '}';
    }
}
