package com.apphunt.app.event_bus.events.ui.collections;

/**
 * Created by nmp on 15-7-7.
 */
public class SaveCollectionEvent {
    private String collectionId;

    public SaveCollectionEvent(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }
}
