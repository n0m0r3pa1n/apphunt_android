package com.apphunt.app.event_bus.events.api.apps;

/**
 * Created by nmp on 15-5-12.
 */
public class AppSavedApiEvent {
    private int statusCode;

    public AppSavedApiEvent(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
