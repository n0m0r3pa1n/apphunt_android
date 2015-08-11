package com.apphunt.app.api.apphunt.requests.tags;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.CollectionsSearchResultEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetCollectionsByTagsRequest extends BaseGetRequest<AppsCollections> {

    public GetCollectionsByTagsRequest(String tags, int page, int pageSize, Response.ErrorListener errorListener) {
        super(BASE_URL + "/app-collections/tags" + tags + "&page=" + page + "&pageSize=" + pageSize,
                errorListener);
    }

    public GetCollectionsByTagsRequest(String tags, int page, int pageSize, String userId, Response.ErrorListener errorListener) {
        super(BASE_URL + "/app-collections/tags" + tags + "&page=" + page + "&pageSize=" + pageSize +
                "&userId=" + userId, errorListener);
    }

    @Override
    public Class<AppsCollections> getParsedClass() {
        return AppsCollections.class;
    }

    @Override
    public void deliverResponse(AppsCollections response) {
        BusProvider.getInstance().post(new CollectionsSearchResultEvent(response));
    }
}
