package com.apphunt.app.api.apphunt.requests.apps;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.requests.base.BasePutRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.FavouriteAppApiEvent;

import org.json.JSONObject;

/**
 * Created by nmp on 15-9-1.
 */
public class FavouriteAppRequest extends BasePutRequest<JSONObject> {
    private String appId;
    public FavouriteAppRequest(String appId, String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/" + appId + "/actions/favourite?userId=" + userId, null, listener);
        this.appId = appId;
    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        BusProvider.getInstance().post(new FavouriteAppApiEvent(appId));
    }
}
