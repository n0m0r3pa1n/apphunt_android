package com.apphunt.app.api.apphunt.models.users;

import com.apphunt.app.ui.models.history.HistoryEventType;
import com.google.gson.JsonObject;

public class HistoryEvent {
    private HistoryEventType type;
    private String text;
    private User user;
    private JsonObject params;

    public HistoryEventType getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public JsonObject getParams() {
        return params;
    }
}
