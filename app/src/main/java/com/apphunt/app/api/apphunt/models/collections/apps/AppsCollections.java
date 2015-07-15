package com.apphunt.app.api.apphunt.models.collections.apps;

import java.util.ArrayList;
import java.util.List;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 5/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class AppsCollections {
    private int totalCount;

    private List<AppsCollection> collections = new ArrayList<>();

    public AppsCollections(List<AppsCollection> collections) {
        this.collections = collections;
    }

    public List<AppsCollection> getCollections() {
        return collections;
    }

    public void setCollections(List<AppsCollection> collections) {
        this.collections = collections;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
