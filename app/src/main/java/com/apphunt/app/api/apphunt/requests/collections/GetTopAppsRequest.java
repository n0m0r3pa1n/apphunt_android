package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetTopAppsCollectionApiEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 5/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetTopAppsRequest extends BaseGetRequest<AppsCollections> {

    public GetTopAppsRequest(String criteria, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/search?q=" + criteria, listener);
    }

    public GetTopAppsRequest(String criteria, String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/search?q=" + criteria + "&userId=" + userId, listener);
    }

    @Override
    public Class<AppsCollections> getParsedClass() {
        return AppsCollections.class;
    }

    @Override
    public void deliverResponse(AppsCollections response) {
        BusProvider.getInstance().post(new GetTopAppsCollectionApiEvent(response));
    }
}
