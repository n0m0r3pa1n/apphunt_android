package com.shtaigaway.apphunt.api.models;

import com.google.gson.annotations.SerializedName;

public class App {
    @SerializedName("_id")
    private String id;
    private String name;
    private String description;
    private String icon;
    @SerializedName("package")
    private String packageName;
    private String votesCount;
    private String shortUrl;
    private String url;

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
        return votesCount;
    }

    public void setVotesCount(String votesCount) {
        this.votesCount = votesCount;
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
}
