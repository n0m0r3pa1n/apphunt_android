package com.apphunt.app.event_bus.events.ui.history;

public class UnseenHistoryEvent {
    private final int count;
    public static final int RESET_COUNTER = 0;

    public UnseenHistoryEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
