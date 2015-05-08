package com.apphunt.app.event_bus.events.votes;

public class UserCommentVoteEvent extends UserAppVoteEvent {

    public UserCommentVoteEvent(boolean isVote) {
        super(isVote);
    }
}
