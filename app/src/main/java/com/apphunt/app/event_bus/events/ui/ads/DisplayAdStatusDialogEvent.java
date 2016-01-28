package com.apphunt.app.event_bus.events.ui.ads;

import com.apphunt.app.api.apphunt.models.ads.UserAdStatus;

/**
 * Created by nmp on 16-1-20.
 */
public class DisplayAdStatusDialogEvent {
    private UserAdStatus userAdStatus;

    public DisplayAdStatusDialogEvent(UserAdStatus userAdStatus) {
        this.userAdStatus = userAdStatus;
    }

    public UserAdStatus getUserAdStatus() {
        return userAdStatus;
    }
}
