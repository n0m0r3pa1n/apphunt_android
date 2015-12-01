package com.apphunt.app.event_bus.events.ui;

/**
 * Created by nmp on 15-12-1.
 */
public class SelectFragmentEvent {
    private int drawerFragmentIndex;
    public SelectFragmentEvent(int drawerFragmentIndex) {
        this.drawerFragmentIndex = drawerFragmentIndex;
    }

    public int getDrawerFragmentIndex() {
        return drawerFragmentIndex;
    }
}
