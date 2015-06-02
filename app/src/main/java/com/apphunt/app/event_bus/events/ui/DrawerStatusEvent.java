package com.apphunt.app.event_bus.events.ui;

/**
 * Created by nmp on 15-6-2.
 */
public class DrawerStatusEvent {
    private boolean shouldLockDrawer;

    public DrawerStatusEvent(boolean shouldLockDrawer) {
        this.shouldLockDrawer = shouldLockDrawer;
    }

    public boolean shouldLockDrawer() {
        return shouldLockDrawer;
    }

    public void setShouldLockDrawer(boolean shouldLockDrawer) {
        this.shouldLockDrawer = shouldLockDrawer;
    }
}
