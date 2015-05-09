package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.App;

/**
 * Created by nmp on 15-5-9.
 */
public class LoadAppDetailsApiEvent {
    private App app;

    public LoadAppDetailsApiEvent(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
