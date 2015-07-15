package com.apphunt.app.event_bus.events.ui.votes;


public class AppVoteEvent {
    private boolean isVote = true;

    public AppVoteEvent(boolean isVote) {
        this.isVote = isVote;
    }

    public boolean isVote() {
        return isVote;
    }
}
