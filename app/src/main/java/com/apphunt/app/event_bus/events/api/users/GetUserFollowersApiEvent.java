package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.User;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetUserFollowersApiEvent {
    private ArrayList<User> followers = new ArrayList<>();

    public GetUserFollowersApiEvent(ArrayList<User> followers) {
        this.followers = followers;
    }

    public ArrayList<User> getFollowers() {
        return followers;
    }
}
