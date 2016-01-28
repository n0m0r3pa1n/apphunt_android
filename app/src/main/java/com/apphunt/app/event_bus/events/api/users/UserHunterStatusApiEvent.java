package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.UserHunterStatus;

/**
 * Created by nmp on 16-1-27.
 */
public class UserHunterStatusApiEvent {

    private UserHunterStatus userHunterStatus;

    public UserHunterStatusApiEvent(UserHunterStatus response) {
        this.userHunterStatus = response;
    }

    public UserHunterStatus getUserHunterStatus() {
        return userHunterStatus;
    }
}
