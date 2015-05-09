package com.apphunt.app.api.apphunt.requests.votes;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Vote;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.VoteForAppEvent;


public class PostAppVoteRequest extends BasePostRequest<Vote> {
    public PostAppVoteRequest(String appId, String userId, Object body, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/votes?appId=" + appId + "&userId=" + userId, null, listener);
    }

    @Override
    public Class<Vote> getParsedAppClass() {
        return Vote.class;
    }

    @Override
    public void deliverResponse(Vote response) {
        BusProvider.getInstance().post(new VoteForAppEvent(response));
    }
}
