package com.apphunt.app.api.apphunt.models.collections.hunters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nmp on 15-6-8.
 */
public class HuntersCollection {
    private String name;
    private String description;

    @SerializedName("usersDetails")
    private List<Hunter> hunters;

    public HuntersCollection(String name, String description, List<Hunter> hunters) {
        this.name = name;
        this.description = description;
        this.hunters = hunters;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Hunter> getHunters() {
        return hunters;
    }
}
