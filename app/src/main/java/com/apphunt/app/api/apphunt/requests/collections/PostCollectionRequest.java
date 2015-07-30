package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.NewCollection;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.CreateCollectionApiEvent;

public class PostCollectionRequest extends BasePostRequest<AppsCollection> {
    public PostCollectionRequest(NewCollection collection, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections", collection, listener);
    }

    @Override
    public Class<AppsCollection> getParsedClass() {
        return AppsCollection.class;
    }

    @Override
    public void deliverResponse(AppsCollection response) {
        BusProvider.getInstance().post(new CreateCollectionApiEvent(response));
    }
}
