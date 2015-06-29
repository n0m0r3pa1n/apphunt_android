package com.apphunt.app.api.apphunt.requests.votes;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.votes.AppVote;
import com.apphunt.app.api.apphunt.requests.base.BaseGsonRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.AppVoteApiEvent;


public class DeleteAppVoteRequest extends BaseGsonRequest<AppVote> {
    private String appId;
    public DeleteAppVoteRequest(String appId, String userId, Response.ErrorListener listener) {
        super(Method.DELETE, BASE_URL + "/apps/votes?appId=" + appId + "&userId=" + userId, listener);
        this.appId = appId;
    }

    @Override
    public Class<AppVote> getParsedClass() {
        return AppVote.class;
    }

    @Override
    public void deliverResponse(AppVote response) {
        response.setAppId(appId);
        BusProvider.getInstance().post(new AppVoteApiEvent(response, false));
    }
}
