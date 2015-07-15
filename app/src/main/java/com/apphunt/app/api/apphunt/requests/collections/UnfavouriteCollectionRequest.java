package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.requests.base.BaseGsonRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.UnfavouriteCollectionEvent;

import org.json.JSONObject;

public class UnfavouriteCollectionRequest extends BaseGsonRequest<JSONObject> {
    private String collectionId;
    public UnfavouriteCollectionRequest(String collectionId, String userId, Response.ErrorListener listener) {
        super(Method.DELETE, BASE_URL + "/app-collections/" + collectionId +"/actions/favourite?userId=" + userId, listener);
        this.collectionId = collectionId;
    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        BusProvider.getInstance().post(new UnfavouriteCollectionEvent(collectionId, getRawResponse().statusCode));
    }
}
