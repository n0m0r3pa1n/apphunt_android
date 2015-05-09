package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.Vote;

/**
 * Created by nmp on 15-5-9.
 */
public class VoteForAppEvent {
    private Vote vote;

    public VoteForAppEvent(Vote vote) {
        this.vote = vote;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}
