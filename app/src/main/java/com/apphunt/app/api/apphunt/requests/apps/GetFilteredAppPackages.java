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
    public GetFilteredAppPackages(Packages packages, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/actions/filter", packages, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse " + error);
            }
        });
    }

    @Override
    public Class<Packages> getParsedAppClass() {
        return Packages.class;
    }

    @Override
    public void deliverResponse(Packages response) {
        BusProvider.getInstance().post(new PackagesFilteredApiEvent(response));
    }
}
