package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;

/**
 * Created by nmp on 15-9-29.
 */
public class GetCollectionApiEvent {
    private AppsCollection response;

    public GetCollectionApiEvent(AppsCollection response) {
        this.response = response;
    }

    public AppsCollection getResponse() {
        return response;
    }
}
