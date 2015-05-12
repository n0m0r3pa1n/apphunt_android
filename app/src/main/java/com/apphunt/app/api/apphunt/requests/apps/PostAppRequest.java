package com.apphunt.app.api.apphunt.requests.apps;

import android.util.Log;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.AppSavedApiEvent;

import org.json.JSONObject;


public class PostAppRequest extends BasePostRequest<JSONObject> {
    public PostAppRequest(Object body, Response.ErrorListener listener) {
        super(BASE_URL + "/apps", body, listener);
    }

    @Override
    public Class<JSONObject> getParsedAppClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        Log.d(TAG, "deliverResponse " + getRawResponse());
        BusProvider.getInstance().post(new AppSavedApiEvent(getRawResponse().statusCode));
    }
}
