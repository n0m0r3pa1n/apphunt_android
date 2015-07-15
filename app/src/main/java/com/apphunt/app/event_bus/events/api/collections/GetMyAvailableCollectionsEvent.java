package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;

/**
 * Created by nmp on 15-7-13.
 */
public class GetMyAvailableCollectionsEvent {
    private AppsCollections appsCollection;

    public GetMyAvailableCollectionsEvent(AppsCollections appsCollection) {
        this.appsCollection = appsCollection;
    }

    public AppsCollections getAppsCollection() {
        return appsCollection;
    }

    public void setAppsCollection(AppsCollections appsCollection) {
        this.appsCollection = appsCollection;
    }
}
