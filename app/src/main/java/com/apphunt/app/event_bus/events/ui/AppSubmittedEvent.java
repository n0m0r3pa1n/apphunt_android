package com.apphunt.app.event_bus.events.ui;

/**
 * Created by nmp on 15-10-25.
 */
public class AppSubmittedEvent {
    private String packageName;

    public AppSubmittedEvent(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }
}
