package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionsSearchResultEvent {

    private AppsCollections collections;

    public CollectionsSearchResultEvent(AppsCollections collections) {
        this.collections = collections;
    }

    public AppsCollections getCollections() {
        return collections;
    }

    public void setCollections(AppsCollections collections) {
        this.collections = collections;
    }
}