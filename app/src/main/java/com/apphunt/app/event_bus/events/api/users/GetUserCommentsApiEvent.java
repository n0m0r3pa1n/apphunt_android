package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.comments.Comments;

public class GetUserCommentsApiEvent {
    private Comments comments;

    public GetUserCommentsApiEvent(Comments comments) {
        this.comments = comments;
    }

    public Comments getComments() {
        return comments;
    }
}
