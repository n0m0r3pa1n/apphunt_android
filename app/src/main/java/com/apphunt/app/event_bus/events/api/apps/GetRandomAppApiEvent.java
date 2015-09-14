package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.apps.App;

/**
 * Created by nmp on 15-9-14.
 */
public class GetRandomAppApiEvent {
    private App app;

    public GetRandomAppApiEvent(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }
}
