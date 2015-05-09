package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.Comments;

public class LoadAppCommentsEvent {
    private Comments comments;

    public LoadAppCommentsEvent(Comments comments) {
        this.comments = comments;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }
}
