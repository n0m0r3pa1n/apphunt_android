package com.apphunt.app.api.apphunt.models.users;

public class UserProfile extends User {
    private int apps, votes;
    private int comments, collections;
    private int score;

    public int getApps() {
        return apps;
    }

    public int getVotes() {
        return votes;
    }

    public int getComments() {
        return comments;
    }

    public int getCollections() {
        return collections;
    }

    public int getScore() {
        return score;
    }
}
