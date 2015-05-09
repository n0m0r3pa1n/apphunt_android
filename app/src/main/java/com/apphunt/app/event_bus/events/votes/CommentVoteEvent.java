package com.apphunt.app.event_bus.events.votes;

public class CommentVoteEvent extends AppVoteEvent {

    public CommentVoteEvent(boolean isVote) {
        super(isVote);
    }
}
