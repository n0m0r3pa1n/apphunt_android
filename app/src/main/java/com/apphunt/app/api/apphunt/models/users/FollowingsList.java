package com.apphunt.app.api.apphunt.models.users;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowingsList {
    private ArrayList<User> followings = new ArrayList<>();

    public ArrayList<User> getFollowings() {
        return followings;
    }

    public void setFollowings(ArrayList<User> followings) {
        this.followings = followings;
    }

    @Override
    public String toString() {
        return "FollowingsList{" +
                "followings=" + followings +
                '}';
    }
}
