package com.apphunt.app.api.apphunt.requests.apps;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.google.gson.JsonArray;

/**
 * Created by nmp on 15-11-17.
 */
public class GetAppsByPackages extends BasePostRequest<JsonArray> {
    private Response.Listener<JsonArray> listener1;

    public GetAppsByPackages(Object packages, Response.Listener<JsonArray> listener1, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/packages", packages, listener);
        this.listener1 = listener1;
    }

    @Override
    public Class<JsonArray> getParsedClass() {
        return JsonArray.class;
    }

    @Override
    public void deliverResponse(JsonArray response) {
        listener1.onResponse(response);
    }
}
