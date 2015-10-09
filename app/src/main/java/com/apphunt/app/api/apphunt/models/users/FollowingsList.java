package com.apphunt.app.api.apphunt.models.users;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowingsList {
    private ArrayList<User> following = new ArrayList<>();
    private int page, pageSize, totalCount;

    public ArrayList<User> getFollowing() {
        return following;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public String toString() {
        return "FollowingsList{" +
                "following=" + following +
                '}';
    }
}
