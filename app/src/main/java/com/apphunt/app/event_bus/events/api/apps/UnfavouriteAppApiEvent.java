package com.apphunt.app.event_bus.events.api.apps;

public class UnfavouriteAppApiEvent {
    private String appId;

    public UnfavouriteAppApiEvent(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }
}
