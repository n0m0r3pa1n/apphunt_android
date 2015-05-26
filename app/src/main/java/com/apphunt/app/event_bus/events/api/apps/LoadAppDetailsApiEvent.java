package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.apps.App;

/**
 * Created by nmp on 15-5-9.
 */
public class LoadAppDetailsApiEvent {
    private App baseApp;

    public LoadAppDetailsApiEvent(App baseApp) {
        this.baseApp = baseApp;
    }

    public App getBaseApp() {
        return baseApp;
    }

    public void setBaseApp(App baseApp) {
        this.baseApp = baseApp;
    }
}
