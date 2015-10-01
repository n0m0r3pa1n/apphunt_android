package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.UsersList;
import com.apphunt.app.constants.Constants.LoginProviders;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/19/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetFilterUsersApiEvent {
    private UsersList users;
    private final LoginProviders provider;

    public GetFilterUsersApiEvent(UsersList users, LoginProviders provider) {
        this.users = users;
        this.provider = provider;
    }

    public UsersList getUsers() {
        return users;
    }

    public LoginProviders getProvider() {
        return provider;
    }
}
