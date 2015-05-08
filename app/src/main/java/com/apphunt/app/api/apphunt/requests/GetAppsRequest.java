package com.apphunt.app.api.apphunt.requests;

import android.util.Log;

import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.GetAppsEvent;


public class GetAppsRequest extends BaseGsonRequest<AppsList> {
    public GetAppsRequest(String date, String platform, int pageSize, int page) {
        super(Method.GET, BASE_URL + "/apps?date="+date+"&platform="+platform+"&status=approved&pageSize="+pageSize+"&page=" + page, null);
    }

    @Override
    public Class<AppsList> getParsedAppClass() {
        return AppsList.class;
    }

    @Override
    public void deliverResponse(AppsList response) {
        Log.d("TEST", response.toString());
        BusProvider.getInstance().post(new GetAppsEvent(response));
    }


}
