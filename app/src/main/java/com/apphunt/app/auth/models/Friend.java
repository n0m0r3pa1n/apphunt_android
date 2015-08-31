package com.apphunt.app.auth.models;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/25/15.
 * *
 * * NaughtySpirit 2015
 */
public class Friend {
    private String id;
    private String name;
    private String username;
    private String profileImage;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
