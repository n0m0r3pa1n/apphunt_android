package com.apphunt.app.api.apphunt.models.users;

import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable{

    @SerializedName("_id")
    private String id;
    private String advertisingId;
    private String name;
    private String email;
    private String profilePicture;

    private String loginType;
    private String locale;
    private String username;
    private String notificationId;
    private String coverPicture;
    @Expose(serialize = false)
    protected boolean isFollowing;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUsername() {
        return username;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getAdvertisingId() {
        this.advertisingId = SharedPreferencesHelper.getStringPreference(Constants.KEY_ADVERTISING_ID, null);
        return advertisingId;
    }

    public void setAdvertisingId(String advertisingId) {
        this.advertisingId = advertisingId;
        SharedPreferencesHelper.setPreference(Constants.KEY_ADVERTISING_ID, advertisingId);
    }

    //WARN:
    @Override
    public String toString() {
        return "{" +
                "id:'" + id + '\'' +
                ", advertisingId:'" + advertisingId + '\'' +
                ", name:'" + name + '\'' +
                ", email:'" + email + '\'' +
                ", profilePicture:'" + profilePicture + '\'' +
                ", loginType:'" + loginType + '\'' +
                ", locale:'" + locale + '\'' +
                ", username:'" + username + '\'' +
                ", notificationId:'" + notificationId + '\'' +
                ", coverPicture:'" + coverPicture + '\'' +
                ", isFollowing:" + isFollowing +
                '}';
    }
}
