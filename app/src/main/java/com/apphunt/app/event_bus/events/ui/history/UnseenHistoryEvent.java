package com.apphunt.app.event_bus.events.ui.history;

/**
 * Created by nmp on 15-10-2.
 */
public class UnseenHistoryEvent {
    private final int count;
    public static final int RESET_COUNTER = -1;

    public UnseenHistoryEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
