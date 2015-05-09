package com.apphunt.app.event_bus.events.votes;


public class AppVoteEvent {
    private boolean isVote = true;
    private int position = 0;

    public AppVoteEvent(boolean isVote) {
        this.isVote = isVote;
    }

    public AppVoteEvent(boolean isVote, int position) {
        this.isVote = isVote;
        this.position = position;
    }

    public boolean isVote() {
        return isVote;
    }

    public void setIsVote(boolean isVote) {
        this.isVote = isVote;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
