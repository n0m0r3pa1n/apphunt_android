package com.apphunt.app.api.apphunt.requests.tags;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.apps.AppsList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.AppsSearchResultEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetAppsByTagsRequest extends BaseGetRequest<AppsList> {

    public GetAppsByTagsRequest(String tags, int page, int pageSize, Response.ErrorListener errorListener) {
        super(BASE_URL + "/apps/tags" + tags + "&page=" + page + "&pageSize=" + pageSize,
                errorListener);
    }

    public GetAppsByTagsRequest(String tags, int page, int pageSize, String userId, Response.ErrorListener errorListener) {
        super(BASE_URL + "/apps/tags" + tags + "&page=" + page + "&pageSize=" + pageSize +
                "&userId=" + userId, errorListener);
    }

    @Override
    public Class<AppsList> getParsedClass() {
        return AppsList.class;
    }

    @Override
    public void deliverResponse(AppsList response) {
        BusProvider.getInstance().post(new AppsSearchResultEvent(response));
    }
}
