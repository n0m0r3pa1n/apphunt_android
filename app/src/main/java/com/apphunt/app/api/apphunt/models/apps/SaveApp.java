package com.apphunt.app.api.apphunt.models.apps;

import com.google.gson.annotations.SerializedName;

public class SaveApp {

    private String platform;
    private String userId;
    @SerializedName("package")
    private String packageName;
    private String description;
    private String[] tags = {};

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "SaveApp{" +
                "platform='" + platform + '\'' +
                ", userId='" + userId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                '}';
    }
}
