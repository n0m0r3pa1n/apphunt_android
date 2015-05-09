package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.AppsList;


public class LoadAppsEvent {
    private AppsList appsList;

    public LoadAppsEvent(AppsList appsList) {
        this.appsList = appsList;
    }

    public AppsList getAppsList() {
        return appsList;
    }

    public void setAppsList(AppsList appsList) {
        this.appsList = appsList;
    }
}
