package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.apps.BaseAppsList;

/**
 * Created by nmp on 15-12-21.
 */
public class GetTrendingAppsApiEvent {
    private BaseAppsList appList;

    public GetTrendingAppsApiEvent(BaseAppsList appList) {
        this.appList = appList;
    }

    public BaseAppsList getAppList() {
        return appList;
    }
}
