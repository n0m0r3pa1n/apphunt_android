package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.requests.base.BasePutRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionEvent;

import org.json.JSONObject;

public class PutCollectionsRequest extends BasePutRequest<JSONObject> {
    public PutCollectionsRequest(String collectionId, Object body, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/" + collectionId, body, listener);

    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        BusProvider.getInstance().post(new UpdateCollectionEvent(getRawResponse().statusCode));
    }
}
