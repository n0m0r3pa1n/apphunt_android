package com.apphunt.app.api.apphunt.requests.tags;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.SearchItems;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.SearchResultsApiEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/8/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetItemsByTagsRequest extends BaseGetRequest<SearchItems> {

    public GetItemsByTagsRequest(String tags, Response.ErrorListener errorListener) {
        super(BASE_URL + "/tags" + tags, errorListener);
    }

    public GetItemsByTagsRequest(String tags, String userId, Response.ErrorListener errorListener) {
        super(BASE_URL + "/tags" + tags + "&userId=" + userId, errorListener);
    }

    @Override
    public Class<SearchItems> getParsedClass() {
        return SearchItems.class;
    }

    @Override
    public void deliverResponse(SearchItems response) {
        BusProvider.getInstance().post(new SearchResultsApiEvent(response));
    }
}
