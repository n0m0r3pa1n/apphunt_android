package com.apphunt.app.event_bus.events.api.users;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class UserFollowApiEvent {
    private String userId;
    private boolean isSuccess;

    public UserFollowApiEvent(String userId,boolean isSuccess) {
        this.isSuccess = isSuccess;
        this.userId = userId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getUserId() {
        return userId;
    }
}
