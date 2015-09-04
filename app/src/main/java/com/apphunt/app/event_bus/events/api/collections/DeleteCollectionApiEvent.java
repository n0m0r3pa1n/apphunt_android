package com.apphunt.app.event_bus.events.api.collections;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 7/9/15.
 */
public class DeleteCollectionApiEvent {
    private String collectionId;

    public DeleteCollectionApiEvent(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }
}
