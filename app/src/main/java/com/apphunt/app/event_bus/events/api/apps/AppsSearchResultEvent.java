package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.apps.AppsList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class AppsSearchResultEvent {

    private AppsList apps;

    public AppsSearchResultEvent(AppsList apps) {
        this.apps = apps;
    }

    public AppsList getApps() {
        return apps;
    }

    public void setApps(AppsList apps) {
        this.apps = apps;
    }
}
