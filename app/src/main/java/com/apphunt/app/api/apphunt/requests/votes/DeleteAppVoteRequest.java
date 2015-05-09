package com.apphunt.app.api.apphunt.requests.votes;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Vote;
import com.apphunt.app.api.apphunt.requests.base.BaseGsonRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ApiAppVoteEvent;


public class DeleteAppVoteRequest extends BaseGsonRequest<Vote> {
    private String appId;
    public DeleteAppVoteRequest(String appId, String userId, Response.ErrorListener listener) {
        super(Method.DELETE, BASE_URL + "/apps/votes?appId=" + appId + "&userId=" + userId, listener);
        this.appId = appId;
    }

    @Override
    public Class<Vote> getParsedAppClass() {
        return Vote.class;
    }

    @Override
    public void deliverResponse(Vote response) {
        response.setAppId(appId);
        BusProvider.getInstance().post(new ApiAppVoteEvent(response, false));
    }
}
