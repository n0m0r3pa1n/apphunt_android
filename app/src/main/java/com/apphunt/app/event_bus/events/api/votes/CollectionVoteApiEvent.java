package com.apphunt.app.event_bus.events.api.votes;

import com.apphunt.app.api.apphunt.models.votes.CollectionVote;
import com.apphunt.app.api.apphunt.models.votes.CommentVote;

public class CollectionVoteApiEvent {
    private boolean isVote;
    private CollectionVote collectionVote;

    public CollectionVoteApiEvent(CollectionVote collectionVote, boolean isVote) {
        this.isVote = isVote;
        this.collectionVote = collectionVote;
    }

    public boolean isVote() {
        return isVote;
    }

    public void setIsVote(boolean isVote) {
        this.isVote = isVote;
    }

    public CollectionVote getCollectionVote() {
        return collectionVote;
    }

    public void setCollectionVote(CollectionVote collectionVote) {
        this.collectionVote = collectionVote;
    }
}
