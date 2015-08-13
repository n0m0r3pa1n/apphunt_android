package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetMyAvailableCollectionsApiEvent;

/**
 * Created by nmp on 15-7-13.
 */
public class GetMyAvailableCollectionsRequest extends BaseGetRequest<AppsCollections> {
    public GetMyAvailableCollectionsRequest(String userId, String appId, int page, int pageSize, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/available?userId=" + userId + "&appId=" + appId + "&page=" + page + "&pageSize=" + pageSize, listener);
    }

    @Override
    public Class<AppsCollections> getParsedClass() {
        return AppsCollections.class;
    }

    @Override
    public void deliverResponse(AppsCollections response) {
        BusProvider.getInstance().post(new GetMyAvailableCollectionsApiEvent(response));
    }
}
