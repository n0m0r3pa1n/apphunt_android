package com.apphunt.app.api.apphunt.requests.apps;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.requests.base.BaseGsonRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.UnfavouriteAppApiEvent;

import org.json.JSONObject;

public class UnfavouriteAppRequest extends BaseGsonRequest<JSONObject> {
    private String appId;
    public UnfavouriteAppRequest(String appId, String userId, Response.ErrorListener listener) {
        super(Method.DELETE, BASE_URL + "/apps/" + appId +"/actions/favourite?userId=" + userId, listener);
        this.appId = appId;
    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        BusProvider.getInstance().post(new UnfavouriteAppApiEvent(appId));
    }
}
