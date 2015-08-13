package com.apphunt.app.api.apphunt.requests.apps;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apphunt.app.api.apphunt.models.apps.Packages;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.PackagesFilteredApiEvent;


public class GetFilteredAppPackages extends BasePostRequest<Packages> {
    public static final String TAG = GetFilteredAppPackages.class.getSimpleName();
    private Response.Listener<Packages> listener;

    public GetFilteredAppPackages(Packages packages, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/actions/filter", packages, listener);
    }

    public GetFilteredAppPackages(Packages packages, Response.Listener<Packages> listener, Response.ErrorListener errorListener) {
        super(BASE_URL + "/apps/actions/filter", packages, errorListener);
        this.listener = listener;
    }

    @Override
    public Class<Packages> getParsedClass() {
        return Packages.class;
    }

    @Override
    public void deliverResponse(Packages response) {
        if(listener != null) {
            listener.onResponse(response);
        } else {
            BusProvider.getInstance().post(new PackagesFilteredApiEvent(response));
        }
    }
}
