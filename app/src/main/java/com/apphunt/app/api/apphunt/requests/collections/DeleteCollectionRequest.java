package com.apphunt.app.api.apphunt.requests.collections;

import android.util.Log;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.requests.base.BaseGsonRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.DeleteCollectionEvent;
import com.apphunt.app.event_bus.events.api.collections.GetAllCollectionsEvent;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 7/9/15.
 */
public class DeleteCollectionRequest extends BaseGsonRequest<AppsCollection> {

    public DeleteCollectionRequest(String collectionId, Response.ErrorListener listener) {
        super(Method.DELETE, BASE_URL + "/app-collections?collectionId=" + collectionId, listener);
    }


    @Override
    public Class<AppsCollection> getParsedClass() {
        return AppsCollection.class;
    }

    @Override
    public void deliverResponse(AppsCollection response) {
        BusProvider.getInstance().post(new DeleteCollectionEvent());
    }
}
