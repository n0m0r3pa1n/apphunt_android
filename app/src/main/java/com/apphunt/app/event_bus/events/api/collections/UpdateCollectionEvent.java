package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;

public class UpdateCollectionEvent {
    private final AppsCollection appsCollection;

    public UpdateCollectionEvent(AppsCollection appsCollection) {
        this.appsCollection = appsCollection;
    }

    public AppsCollection getAppsCollection() {
        return appsCollection;
    }
}
