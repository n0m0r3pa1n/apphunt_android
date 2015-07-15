package com.apphunt.app.event_bus.events.ui;

/**
 * Created by nmp on 15-6-11.
 */
public class SearchStatusEvent {
    private boolean isSearching;

    public SearchStatusEvent(boolean isSearching) {
        this.isSearching = isSearching;
    }

    public boolean isSearching() {
        return isSearching;
    }
}
