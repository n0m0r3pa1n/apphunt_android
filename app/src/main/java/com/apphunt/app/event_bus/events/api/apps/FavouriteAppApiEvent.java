package com.apphunt.app.event_bus.events.api.apps;

/**
 * Created by nmp on 15-9-1.
 */
public class FavouriteAppApiEvent {
    private String appId;

    public FavouriteAppApiEvent(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }
}
