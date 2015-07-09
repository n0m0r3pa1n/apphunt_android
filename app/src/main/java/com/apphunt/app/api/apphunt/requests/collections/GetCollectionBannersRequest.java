package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.CollectionBannersList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetBannersEvent;

/**
 * Created by nmp on 15-7-9.
 */
public class GetCollectionBannersRequest extends BaseGetRequest<CollectionBannersList> {
    public GetCollectionBannersRequest(Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/banners", listener);
    }

    @Override
    public Class<CollectionBannersList> getParsedClass() {
        return CollectionBannersList.class;
    }

    @Override
    public void deliverResponse(CollectionBannersList response) {
        BusProvider.getInstance().post(new GetBannersEvent(response));
    }
}
