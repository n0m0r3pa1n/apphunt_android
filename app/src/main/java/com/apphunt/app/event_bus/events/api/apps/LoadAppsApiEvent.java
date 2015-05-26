package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.apps.AppsList;


public class LoadAppsApiEvent {
    private AppsList appsList;

    public LoadAppsApiEvent(AppsList appsList) {
        this.appsList = appsList;
    }

    public AppsList getAppsList() {
        return appsList;
    }

    public void setAppsList(AppsList appsList) {
        this.appsList = appsList;
    }
}
