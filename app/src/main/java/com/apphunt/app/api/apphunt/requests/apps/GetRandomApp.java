package com.apphunt.app.api.apphunt.requests.apps;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.GetRandomAppApiEvent;

public class GetRandomApp extends BaseGetRequest<App> {
    public GetRandomApp(Response.ErrorListener listener) {
        super(BASE_URL + "/apps/random", listener);
    }

    public GetRandomApp(String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/random?userId=" + userId, listener);
    }

    @Override
    public Class<App> getParsedClass() {
        return App.class;
    }

    @Override
    public void deliverResponse(App response) {
        BusProvider.getInstance().post(new GetRandomAppApiEvent(response));
    }
}
