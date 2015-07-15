package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.hunters.HuntersCollection;
import com.apphunt.app.api.apphunt.models.collections.hunters.HuntersCollections;

public class GetTopHuntersCollectionEvent {
    private HuntersCollections huntersCollections;
    public GetTopHuntersCollectionEvent(HuntersCollections collection) {
        huntersCollections = collection;
    }

    public HuntersCollections getHuntersCollections() {
        return huntersCollections;
    }
}
