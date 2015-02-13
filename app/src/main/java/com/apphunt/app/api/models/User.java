package com.apphunt.app.api.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    private String id;
    private String loginId;
    private String name;
    private String email;
    private String profilePicture;
    private String loginType;
    private String locale;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String facebookId) {
        this.loginId = facebookId;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", loginId='" + loginId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", loginType='" + loginType + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }
}
