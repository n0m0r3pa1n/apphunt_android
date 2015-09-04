package com.apphunt.app.api.apphunt.requests.tags;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.tags.Tags;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.tags.TagsSuggestionApiEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetTagsSuggestionRequest extends BaseGetRequest<Tags> {

    public GetTagsSuggestionRequest(String str, Response.ErrorListener  errorListener) {
        super(BASE_URL + "/tags/suggest?name=" + str, errorListener);
    }

    @Override
    public Class getParsedClass() {
        return Tags.class;
    }

    @Override
    public void deliverResponse(Tags tagsResponse) {
        BusProvider.getInstance().post(new TagsSuggestionApiEvent(tagsResponse));
    }
}
