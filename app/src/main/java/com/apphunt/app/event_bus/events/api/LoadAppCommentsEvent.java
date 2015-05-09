package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.Comments;

public class LoadAppCommentsEvent {
    private Comments comments;
    private boolean shouldReload;

    public LoadAppCommentsEvent(Comments comments, boolean shouldReload) {
        this.comments = comments;
        this.shouldReload = shouldReload;

    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public boolean shouldReload() {
        return shouldReload;
    }

    public void setShouldReload(boolean shouldReload) {
        this.shouldReload = shouldReload;
    }
}
