package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.apphunt.app.api.apphunt.requests.base.BasePutRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.FavouriteCollectionEvent;

import org.json.JSONObject;

/**
 * Created by nmp on 15-6-29.
 */
public class FavouriteCollectionRequest extends BasePutRequest<JSONObject> {
    private String collectionId;
    public FavouriteCollectionRequest(String collectionId, String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/" + collectionId +"/actions/favourite?userId=" + userId, null, listener);
        this.collectionId = collectionId;
    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        BusProvider.getInstance().post(new FavouriteCollectionEvent(collectionId, getRawResponse().statusCode));
    }
}
