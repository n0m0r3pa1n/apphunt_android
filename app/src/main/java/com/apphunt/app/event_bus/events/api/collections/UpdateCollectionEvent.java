package com.apphunt.app.event_bus.events.api.collections;

public class UpdateCollectionEvent {
    private int statusCode;

    public UpdateCollectionEvent(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
