package com.apphunt.app.api.apphunt.models.apps;

import com.google.gson.annotations.SerializedName;

public class SaveApp {

    private String platform;
    private String userId;
    @SerializedName("package")
    private String packageName;
    private String description;

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
}
