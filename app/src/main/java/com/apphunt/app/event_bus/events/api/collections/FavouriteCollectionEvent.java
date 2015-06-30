package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;

/**
 * Created by nmp on 15-6-29.
 */
public class FavouriteCollectionEvent {
    private int statusCode;
    private AppsCollection collection;

    public FavouriteCollectionEvent(AppsCollection collection, int statusCode) {
        this.statusCode = statusCode;
        this.collection = collection;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public AppsCollection getCollection() {
        return collection;
    }
}
