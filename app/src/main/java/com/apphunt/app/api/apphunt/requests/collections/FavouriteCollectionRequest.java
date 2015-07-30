package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.requests.base.BasePutRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.FavouriteCollectionApiEvent;

import org.json.JSONObject;

/**
 * Created by nmp on 15-6-29.
 */
public class FavouriteCollectionRequest extends BasePutRequest<JSONObject> {

    private AppsCollection collection;

    public FavouriteCollectionRequest(AppsCollection collection, String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/" + collection.getId() +"/actions/favourite?userId=" + userId, null, listener);
        this.collection = collection;
    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        collection.setIsFavourite(true);
        BusProvider.getInstance().post(new FavouriteCollectionApiEvent(collection, getRawResponse().statusCode));
    }
}
