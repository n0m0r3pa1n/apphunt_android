package com.apphunt.app.api.apphunt.requests.ads;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.ads.Ad;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ads.GetAdApiEvent;

public class GetAdRequest extends BaseGetRequest<Ad> {
    public GetAdRequest(Response.ErrorListener listener) {
        super(BASE_URL + "/ad", listener);
    }

    @Override
    public Class<Ad> getParsedClass() {
        return Ad.class;
    }

    @Override
    public void deliverResponse(Ad response) {
        BusProvider.getInstance().post(new GetAdApiEvent(response));
    }
}
