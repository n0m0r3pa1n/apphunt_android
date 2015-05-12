package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.AppsList;

public class LoadSearchedAppsApiEvent {
    private AppsList appsList;

    public LoadSearchedAppsApiEvent(AppsList appsList) {
        this.appsList = appsList;
    }

    public AppsList getAppsList() {
        return appsList;
    }

    public void setAppsList(AppsList appsList) {
        this.appsList = appsList;
    }
}
