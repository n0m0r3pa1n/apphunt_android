package com.apphunt.app.event_bus.events.api.ads;

import com.apphunt.app.api.apphunt.models.ads.UserAdStatus;

/**
 * Created by nmp on 16-1-20.
 */
public class UserAdStatusApiEvent {
    private UserAdStatus userAdStatus;

    public UserAdStatusApiEvent(UserAdStatus userAdStatus) {
        this.userAdStatus = userAdStatus;
    }

    public UserAdStatus getUserAdStatus() {
        return userAdStatus;
    }
}
