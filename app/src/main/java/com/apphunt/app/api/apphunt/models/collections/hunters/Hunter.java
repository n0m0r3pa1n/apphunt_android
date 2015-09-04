package com.apphunt.app.api.apphunt.models.collections.hunters;

import com.apphunt.app.api.apphunt.models.users.User;

/**
 * Created by nmp on 15-6-8.
 */
public class Hunter {
    private User user;
    private int votes, comments, addedApps, collections, score;

    public Hunter(User user, int votes, int comments, int addedApps, int collections, int score) {
        this.user = user;
        this.votes = votes;
        this.comments = comments;
        this.addedApps = addedApps;
        this.collections = collections;
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public int getVotes() {
        return votes;
    }

    public int getComments() {
        return comments;
    }

    public int getAddedApps() {
        return addedApps;
    }

    public int getCollections() {
        return collections;
    }

    public int getScore() {
        return score;
    }
}
