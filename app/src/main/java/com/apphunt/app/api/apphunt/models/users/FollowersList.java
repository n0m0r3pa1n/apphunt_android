package com.apphunt.app.api.apphunt.models.users;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowersList {
    private ArrayList<User> followers = new ArrayList<>();

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }

    @Override
    public String toString() {
        return "Followers{" +
                "followers=" + followers +
                '}';
    }
}
