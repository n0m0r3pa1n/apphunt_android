package com.apphunt.app.api.apphunt.requests.ads;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apphunt.app.api.apphunt.models.ads.AdStatus;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ads.AdStatusApiEvent;
import com.apphunt.app.event_bus.events.api.ads.AdStatusErrorApiEvent;

/**
 * Created by nmp on 16-1-19.
 */
public class GetAdStatusRequest extends BaseGetRequest<AdStatus> {
    private String appPackage;
    public GetAdStatusRequest(String userId, int adLoadNumber, final String appPackage) {
        super(BASE_URL + "/ads/status?userId=" + userId + "&adLoadNumber=" + adLoadNumber, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BusProvider.getInstance().post(new AdStatusErrorApiEvent(appPackage));
            }
        });
        this.appPackage = appPackage;
    }

    @Override
    public Class<AdStatus> getParsedClass() {
        return AdStatus.class;
    }

    @Override
    public void deliverResponse(AdStatus response) {
        BusProvider.getInstance().post(new AdStatusApiEvent(response, appPackage));
    }
}
