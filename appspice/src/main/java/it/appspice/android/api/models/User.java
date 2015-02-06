package it.appspice.android.api.models;

import com.google.gson.annotations.Expose;

/**
 * Created by nmp on 1/14/15.
 */
public class User {
    @Expose
    private String id;

    public User(String id) {
        this.id = id;
    }
}
