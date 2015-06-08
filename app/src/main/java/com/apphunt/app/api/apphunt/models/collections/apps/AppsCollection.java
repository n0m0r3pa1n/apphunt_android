package com.apphunt.app.api.apphunt.models.collections.apps;

import com.apphunt.app.api.apphunt.models.apps.BaseApp;
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

    public List<BaseApp> getApps() {
        return apps;
    }

    public void setApps(List<BaseApp> apps) {
        this.apps = apps;
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
