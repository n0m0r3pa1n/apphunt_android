package com.apphunt.app.api.apphunt.requests.apps;

import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadAppsApiEvent;


public class GetAppsRequest extends BaseGetRequest<AppsList> {

    public GetAppsRequest(String date, String platform, int pageSize, int page) {
        super(BASE_URL + "/apps?date="+date+"&platform="+platform+"&status=approved&pageSize="+pageSize+"&page=" + page, null);
    }

    public GetAppsRequest(String date, String userId,String platform, int pageSize, int page) {
        super(BASE_URL + "/apps?userId="+userId+"&date="+date+"&platform="+platform+"&status=approved&pageSize="+pageSize+"&page=" + page, null);
    }

    @Override
    public Class<AppsList> getParsedAppClass() {
        return AppsList.class;
    }

    @Override
    public void deliverResponse(AppsList response) {
        BusProvider.getInstance().post(new LoadAppsApiEvent(response));
    }
}
