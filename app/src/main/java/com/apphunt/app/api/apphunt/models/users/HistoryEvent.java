package com.apphunt.app.api.apphunt.models.users;

import com.apphunt.app.ui.models.history.HistoryEventType;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoryEvent {
    private String dateRegExp = "^(\\d{4}-\\d{1,2}-\\d{1,2}).+$";

    @SerializedName("_id")
    private String id;
    private String createdAt;
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

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        Pattern p = Pattern.compile(dateRegExp);
        Matcher m = p.matcher(createdAt);
        if(m.matches()) {
            return m.group(1);
        }
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
