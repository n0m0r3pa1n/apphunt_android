package com.apphunt.app.event_bus.events.ui;

import com.apphunt.app.api.apphunt.models.comments.Comment;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 11/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class ReplyToCommentEvent {
    private Comment comment;

    public ReplyToCommentEvent(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }
}
