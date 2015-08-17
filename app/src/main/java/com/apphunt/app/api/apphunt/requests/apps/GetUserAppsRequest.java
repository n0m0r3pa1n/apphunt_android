package com.apphunt.app.api.apphunt.requests.apps;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.apps.AppsList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserAppsApiEvent;

public class GetUserAppsRequest extends BaseGetRequest<AppsList> {
    public GetUserAppsRequest(String userId, Pagination pagination, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/mine?userId=" + userId + "&" + pagination.getPaginationString(), listener);
    }

    @Override
    public Class<AppsList> getParsedClass() {
        return AppsList.class;
    }

    @Override
    public void deliverResponse(AppsList response) {
        BusProvider.getInstance().post(new GetUserAppsApiEvent(response));
    }
}
