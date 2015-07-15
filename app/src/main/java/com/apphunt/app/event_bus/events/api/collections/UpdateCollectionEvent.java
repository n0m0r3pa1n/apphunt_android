package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;

public class UpdateCollectionEvent {
    private final AppsCollection appsCollection;
    private boolean isSuccess = true;

    public UpdateCollectionEvent(AppsCollection appsCollection) {
        this.appsCollection = appsCollection;
    }

    public UpdateCollectionEvent(AppsCollection appsCollection, boolean isSuccess) {
        this.appsCollection = appsCollection;
        this.isSuccess = isSuccess;
    }

    public AppsCollection getAppsCollection() {
        return appsCollection;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
