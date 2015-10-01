package com.apphunt.app.api.apphunt.models.users;

public class UserProfile extends User {
    private int apps, votes;
    private int comments, collections;
    private int score;
    private int favouriteApps, favouriteCollections;
    private int followingCount, followersCount;

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

    public int getFollowingCount() {
        return followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "apps=" + apps +
                ", votes=" + votes +
                ", comments=" + comments +
                ", collections=" + collections +
                ", score=" + score +
                ", favouriteApps=" + favouriteApps +
                ", favouriteCollections=" + favouriteCollections +
                ", isFollowing=" + isFollowing +
                ", followingCount=" + followingCount +
                ", followersCount=" + followersCount +
                '}';
    }
}
