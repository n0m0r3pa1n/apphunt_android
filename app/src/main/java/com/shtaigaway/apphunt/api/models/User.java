package com.shtaigaway.apphunt.api.models;

import com.google.gson.annotations.Expose;

public class User {
    @Expose
    private String advertisingId;

    public User(String advertisingId) {
        this.advertisingId = advertisingId;
    }

    public String getAdvertisingId() {
        return advertisingId;
    }

    public void setAdvertisingId(String advertisingId) {
        this.advertisingId = advertisingId;
    }
}
