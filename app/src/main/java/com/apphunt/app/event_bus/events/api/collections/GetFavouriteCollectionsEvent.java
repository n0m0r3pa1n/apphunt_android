package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;

/**
 * Created by nmp on 15-6-29.
 */
public class GetFavouriteCollectionsEvent extends GetTopAppsCollectionEvent{
    public GetFavouriteCollectionsEvent(AppsCollections appsCollection) {
        super(appsCollection);
    }
}
