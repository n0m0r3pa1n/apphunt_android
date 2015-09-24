package com.apphunt.app.api.apphunt.models.users;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/22/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowingsList {
    ArrayList<String> followerIds = new ArrayList<>();

    public ArrayList<String> getFollowerIds() {
        return followerIds;
    }

    public void setFollowerIds(ArrayList<String> followerIds) {
        this.followerIds = followerIds;
    }

    public void addId(String id) {
        followerIds.add(id);
    }
}
