package com.apphunt.app.api.apphunt.requests.version;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.version.Version;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.version.GetAppVersionApiEvent;

/**
 * Created by nmp on 15-7-30.
 */
public class GetLatestAppVersionRequest extends BaseGetRequest<Version> {
    public GetLatestAppVersionRequest(Response.ErrorListener listener) {
        super(BASE_URL + "/app/version", listener);
    }

    @Override
    public Class<Version> getParsedClass() {
        return Version.class;
    }

    @Override
    public void deliverResponse(Version response) {
        BusProvider.getInstance().post(new GetAppVersionApiEvent(response));
    }
}
