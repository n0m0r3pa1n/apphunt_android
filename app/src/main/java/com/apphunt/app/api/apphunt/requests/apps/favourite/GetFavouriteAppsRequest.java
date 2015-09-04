package com.apphunt.app.api.apphunt.requests.apps.favourite;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.apps.AppsList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.GetFavouriteAppsApiEvent;

public class GetFavouriteAppsRequest extends BaseGetRequest<AppsList>{

    public GetFavouriteAppsRequest(String creatorId, Pagination pagination, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + creatorId + "/favourite-apps?" + pagination.getPaginationString(), listener);
    }

    public GetFavouriteAppsRequest(String creatorId, String userId, Pagination pagination, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + creatorId + "/favourite-apps?userId="+userId+ "&" + pagination.getPaginationString(), listener);
    }

    @Override
    public Class<AppsList> getParsedClass() {
        return AppsList.class;
    }

    @Override
    public void deliverResponse(AppsList response) {
        BusProvider.getInstance().post(new GetFavouriteAppsApiEvent(response));
    }
}
