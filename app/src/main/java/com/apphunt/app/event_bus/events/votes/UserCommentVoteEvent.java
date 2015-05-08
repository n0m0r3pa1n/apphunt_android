package com.apphunt.app.event_bus.events.votes;

/**
 * Created by nmp on 15-5-8.
 */
public class UserCommentVoteEvent extends UserAppVoteEvent {

    public UserCommentVoteEvent(boolean isVote) {
        super(isVote);
    }
}
