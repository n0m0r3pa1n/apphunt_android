package com.apphunt.app.api.apphunt.models.collections;

import java.util.Arrays;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 6/30/15.
 * *
 * * NaughtySpirit 2015
 */
public class NewCollection {
    private String userId;
    private String name;
    private String description;
    private String picture;
    private String[] tags = {};

    public NewCollection() {}

    public NewCollection(String userId, String name, String description, String picture) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "NewCollection{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
