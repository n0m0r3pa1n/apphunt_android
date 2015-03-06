package com.apphunt.app.api.twitter.models;

import com.twitter.sdk.android.core.models.User;

import java.util.List;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/6/15.
 */
public class Friends {
    List<User> users;

    public List<User> getUsers() {
        return users;
    }
}

