package com.apphunt.app.event_bus.events.api.votes;

import com.apphunt.app.api.apphunt.models.votes.AppVote;

/**
 * Created by nmp on 15-5-9.
 */
public class AppVoteApiEvent {
    private AppVote vote;
    private boolean isVote;

    public AppVoteApiEvent(AppVote vote, boolean isVote) {
        this.vote = vote;
        this.isVote = isVote;
    }

    public AppVote getVote() {
        return vote;
    }

    public void setVote(AppVote vote) {
        this.vote = vote;
    }

    public boolean isVote() {
        return isVote;
    }

    public void setIsVote(boolean isVote) {
        this.isVote = isVote;
    }
}
