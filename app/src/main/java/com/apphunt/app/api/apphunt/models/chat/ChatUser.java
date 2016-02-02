package com.apphunt.app.api.apphunt.models.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nmp on 16-1-28.
 */
public class ChatUser {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("advertisingId")
    @Expose
    private String advertisingId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("profilePicture")
    @Expose
    private String profilePicture;

    @SerializedName("username")
    @Expose
    private String username;

    public ChatUser(String id, String name, String email, String username, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getUsername() {
        return username;
    }
}
