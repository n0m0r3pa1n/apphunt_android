package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.FollowersList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetFollowersApiEvent {
    private FollowersList followers;

    public GetFollowersApiEvent(FollowersList followers) {
        this.followers = followers;
    }

    public FollowersList getFollowers() {
        return followers;
    }
}
