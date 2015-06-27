package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;

/**
 * Created by nmp on 15-6-26.
 */
public class GetAllCollectionsEvent extends GetTopAppsCollectionEvent {
    public GetAllCollectionsEvent(AppsCollections appsCollection) {
        super(appsCollection);
    }
}
