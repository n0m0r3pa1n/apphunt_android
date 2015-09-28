package com.apphunt.app.api.apphunt.models.users;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/22/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowingsList {
    ArrayList<String> followingIds = new ArrayList<>();

    public ArrayList<String> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(ArrayList<String> followingIds) {
        this.followingIds = followingIds;
    }

    public void addId(String id) {
        followingIds.add(id);
    }
}
