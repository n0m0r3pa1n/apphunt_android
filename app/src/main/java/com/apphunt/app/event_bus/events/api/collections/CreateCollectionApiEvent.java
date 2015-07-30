package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;

/**
 * Created by nmp on 15-6-30.
 */
public class CreateCollectionApiEvent {
    private AppsCollection appsCollection;

    public CreateCollectionApiEvent(AppsCollection appsCollection) {
        this.appsCollection = appsCollection;
    }

    public AppsCollection getAppsCollection() {
        return appsCollection;
    }
}
