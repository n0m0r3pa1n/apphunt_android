package com.apphunt.app.api.apphunt.requests.apps;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.apps.AppsList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadSearchedAppsApiEvent;
import com.apphunt.app.constants.Constants;

public class GetSearchedAppsRequest extends BaseGetRequest<AppsList>{
    public GetSearchedAppsRequest(String query, int page, int pageSize, String platform, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/search?q=" + query + "&page=" + page + "&pageSize=" + pageSize + "&platform=" + Constants.PLATFORM, listener);
    }

    public GetSearchedAppsRequest(String query, String userId, int page, int pageSize, String platform, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/search?userId=" +userId+ "&q=" + query + "&page=" + page + "&pageSize=" + pageSize + "&platform=" + Constants.PLATFORM, listener);
    }

    @Override
    public Class<AppsList> getParsedClass() {
        return AppsList.class;
    }

    @Override
    public void deliverResponse(AppsList response) {
        BusProvider.getInstance().post(new LoadSearchedAppsApiEvent(response));
    }
}
