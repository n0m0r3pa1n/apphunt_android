package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.AppsList;

/**
 * Created by nmp on 15-5-8.
 */
public class GetAppsEvent {
    private AppsList appsList;

    public GetAppsEvent(AppsList appsList) {
        this.appsList = appsList;
    }

    public AppsList getAppsList() {
        return appsList;
    }

    public void setAppsList(AppsList appsList) {
        this.appsList = appsList;
    }
}
