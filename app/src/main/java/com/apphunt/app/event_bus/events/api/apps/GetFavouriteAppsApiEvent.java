package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.apps.AppsList;

/**
 * Created by nmp on 15-9-1.
 */
public class GetFavouriteAppsApiEvent {
    private AppsList appsList;

    public GetFavouriteAppsApiEvent(AppsList appsList) {
        this.appsList = appsList;
    }

    public AppsList getAppsList() {
        return appsList;
    }
}
