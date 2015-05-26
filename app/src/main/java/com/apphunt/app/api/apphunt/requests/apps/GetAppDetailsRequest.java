package com.apphunt.app.api.apphunt.requests.apps;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadAppDetailsApiEvent;

public class GetAppDetailsRequest extends BaseGetRequest<App> {
    public GetAppDetailsRequest(String appId, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/" + appId, listener);
    }

    public GetAppDetailsRequest(String appId, String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/" + appId + "?userId=" + userId, listener);
    }

    @Override
    public Class<App> getParsedAppClass() {
        return App.class;
    }

    @Override
    public void deliverResponse(App response) {
        BusProvider.getInstance().post(new LoadAppDetailsApiEvent(response));
    }
}
