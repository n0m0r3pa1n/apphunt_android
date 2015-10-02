package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.User;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetUserFollowingsApiEvent {
    private ArrayList<User> followings = new ArrayList<>();

    public GetUserFollowingsApiEvent(ArrayList<User> followings) {
        this.followings = followings;
    }

    public ArrayList<User> getFollowings() {
        return followings;
    }
}
