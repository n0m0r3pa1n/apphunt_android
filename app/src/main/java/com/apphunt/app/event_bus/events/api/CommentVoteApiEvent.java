package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.CommentVote;

/**
 * Created by nmp on 15-5-9.
 */
public class CommentVoteApiEvent {
    private boolean isVote;
    private CommentVote commentVote;

    public CommentVoteApiEvent(CommentVote commentVote, boolean isVote) {
        this.isVote = isVote;
        this.commentVote = commentVote;
    }

    public boolean isVote() {
        return isVote;
    }

    public void setIsVote(boolean isVote) {
        this.isVote = isVote;
    }

    public CommentVote getCommentVote() {
        return commentVote;
    }

    public void setCommentVote(CommentVote commentVote) {
        this.commentVote = commentVote;
    }
}
