package com.apphunt.app.api.apphunt.requests;

import android.util.Log;

import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.GetAppsEvent;

/**
 * Created by nmp on 15-5-8.
 */
public class GetAppsRequest extends BaseGsonRequest<AppsList> {
    public GetAppsRequest(String cityName) {
        super(Method.GET, BASE_URL + "/apps?date=2015-3-19&platform=Android&status=approved&pageSize=5&page=1", null);
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
