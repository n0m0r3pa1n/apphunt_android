package com.apphunt.app.api.apphunt.requests.apps;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.apps.BaseAppsList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.GetTrendingAppsApiEvent;

/**
 * Created by nmp on 15-12-21.
 */
public class GetTrendingAppsRequest extends BaseGetRequest<BaseAppsList> {

    public GetTrendingAppsRequest(int page, int pageSize, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/trending?page=" + page + "&pageSize=" + pageSize, listener);
        setRetryPolicy(new DefaultRetryPolicy(5000, 1, 2));
    }

    public GetTrendingAppsRequest(String userId, int page, int pageSize, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/trending?userId=" + userId + "&page=" + page + "&pageSize=" + pageSize, listener);
        setRetryPolicy(new DefaultRetryPolicy(5000, 1, 2));
    }

    @Override
    public Class<BaseAppsList> getParsedClass() {
        return BaseAppsList.class;
    }

    @Override
    public void deliverResponse(BaseAppsList response) {
        BusProvider.getInstance().post(new GetTrendingAppsApiEvent(response));
    }
}
