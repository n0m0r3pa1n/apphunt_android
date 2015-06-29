package com.apphunt.app.event_bus.events.api.collections;

/**
 * Created by nmp on 15-6-29.
 */
public class UnfavouriteCollectionEvent extends FavouriteCollectionEvent {
    public UnfavouriteCollectionEvent(String collectionId, int statusCode) {
        super(collectionId, statusCode);
    }
}
