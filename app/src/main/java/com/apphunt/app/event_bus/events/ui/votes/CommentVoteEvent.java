package com.apphunt.app.event_bus.events.ui.votes;

public class CommentVoteEvent extends AppVoteEvent {

    public CommentVoteEvent(boolean isVote) {
        super(isVote);
    }
}
