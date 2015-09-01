package com.apphunt.app.api.apphunt.models.users;

public class UserProfile extends User {
    private int apps, votes;
    private int comments, collections;
    private int score;
    private int favouriteApps, favouriteCollections;

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

    public int getFavouriteApps() {
        return favouriteApps;
    }

    public int getFavouriteCollections() {
        return favouriteCollections;
    }
}
