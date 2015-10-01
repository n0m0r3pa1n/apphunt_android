package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetCollectionApiEvent;

public class GetAppCollectionRequest extends BaseGetRequest<AppsCollection> {
    public GetAppCollectionRequest(String collectionId, String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/" + collectionId + "?userId=" + userId, listener);
    }

    public GetAppCollectionRequest(String collectionId, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/" + collectionId, listener);
    }

    @Override
    public Class<AppsCollection> getParsedClass() {
        return AppsCollection.class;
    }

    @Override
    public void deliverResponse(AppsCollection response) {
        BusProvider.getInstance().post(new GetCollectionApiEvent(response));
    }
}
