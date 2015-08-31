package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetFavouriteCollectionsApiEvent;

/**
 * Created by nmp on 15-6-29.
 */
public class GetFavouriteCollectionsRequest extends BaseGetRequest<AppsCollections> {

    public GetFavouriteCollectionsRequest(String favouritedBy, int page, int pageSize, Response.ErrorListener listener) {
        super(BASE_URL + "/users/"+favouritedBy+"/favourite-collections?page=" + page + "&pageSize=" + pageSize, listener);
    }

    public GetFavouriteCollectionsRequest(String favouritedBy, String userId, int page, int pageSize, Response.ErrorListener listener) {
        super(BASE_URL + "/users/"+favouritedBy+"/favourite-collections?userId=" + userId + "&page=" + page + "&pageSize=" + pageSize, listener);
    }

    @Override
    public Class<AppsCollections> getParsedClass() {
        return AppsCollections.class;
    }

    @Override
    public void deliverResponse(AppsCollections response) {
        BusProvider.getInstance().post(new GetFavouriteCollectionsApiEvent(response));
    }
}
