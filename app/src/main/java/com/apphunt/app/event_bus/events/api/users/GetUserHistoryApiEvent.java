package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.HistoryEventsList;

public class GetUserHistoryApiEvent {
    private HistoryEventsList eventsList;

    public GetUserHistoryApiEvent(HistoryEventsList eventsList) {
        this.eventsList = eventsList;
    }

    public HistoryEventsList getEventsList() {
        return eventsList;
    }
}
