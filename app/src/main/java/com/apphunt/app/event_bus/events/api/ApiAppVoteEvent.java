package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.Vote;

/**
 * Created by nmp on 15-5-9.
 */
public class ApiAppVoteEvent {
    private Vote vote;
    private boolean isVote;

    public ApiAppVoteEvent(Vote vote, boolean isVote) {
        this.vote = vote;
        this.isVote = isVote;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public boolean isVote() {
        return isVote;
    }

    public void setIsVote(boolean isVote) {
        this.isVote = isVote;
    }
}
