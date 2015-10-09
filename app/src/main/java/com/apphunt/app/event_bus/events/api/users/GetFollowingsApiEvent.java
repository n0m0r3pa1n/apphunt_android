package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.FollowingsList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetFollowingsApiEvent {
    private FollowingsList following;

    public GetFollowingsApiEvent(FollowingsList following) {
        this.following = following;
    }

    public FollowingsList getFollowing() {
        return following;
    }
}
