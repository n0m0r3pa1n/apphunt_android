package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.comments.Comments;

public class LoadAppCommentsApiEvent {
    private Comments comments;

    public LoadAppCommentsApiEvent(Comments comments) {
        this.comments = comments;

    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }
}
