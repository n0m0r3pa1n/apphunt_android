package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.UsersList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/19/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetFilterUsersApiEvent {
    private UsersList users;

    public GetFilterUsersApiEvent(UsersList users) {
        this.users = users;
    }

    public UsersList getUsers() {
        return users;
    }
}
