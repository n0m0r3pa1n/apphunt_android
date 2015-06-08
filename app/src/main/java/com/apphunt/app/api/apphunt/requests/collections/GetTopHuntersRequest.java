package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.hunters.HuntersCollections;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetTopHuntersCollectionEvent;

public class GetTopHuntersRequest extends BaseGetRequest<HuntersCollections> {
    public GetTopHuntersRequest(String criteria, Response.ErrorListener listener) {
        super(BASE_URL + "/user-collections/search?q=" + criteria, listener);
    }

    @Override
    public Class<HuntersCollections> getParsedAppClass() {
        return HuntersCollections.class;
    }

    @Override
    public void deliverResponse(HuntersCollections response) {
        BusProvider.getInstance().post(new GetTopHuntersCollectionEvent(response));
    }
}
