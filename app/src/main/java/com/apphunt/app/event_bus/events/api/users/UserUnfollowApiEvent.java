package com.apphunt.app.event_bus.events.api.users;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class UserUnfollowApiEvent {
    private boolean isSuccess;

    public UserUnfollowApiEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
