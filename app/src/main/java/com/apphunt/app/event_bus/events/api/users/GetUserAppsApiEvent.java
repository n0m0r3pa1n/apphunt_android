package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.apps.AppsList;

public class GetUserAppsApiEvent {
    private AppsList apps;

    public GetUserAppsApiEvent(AppsList apps) {
        this.apps = apps;
    }

    public AppsList getApps() {
        return apps;
    }
}
