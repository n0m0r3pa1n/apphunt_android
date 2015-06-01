package com.apphunt.app.event_bus.events.ui;

/**
 * Created by nmp on 15-6-1.
 */
public class NetworkStatusChangeEvent {
    private boolean networkState;

    public NetworkStatusChangeEvent(boolean networkState) {
        this.networkState = networkState;
    }

    public boolean isNetworkAvailable() {
        return networkState;
    }
}
