package com.apphunt.app.api.apphunt.models.users;

import java.util.List;

public class HistoryEventsList {
    private String fromDate;
    private List<HistoryEvent> events;

    public List<HistoryEvent> getEvents() {
        return events;
    }

    public String getFromDate() {
        return fromDate;
    }
}
