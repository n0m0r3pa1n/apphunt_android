package com.apphunt.app.api.apphunt.requests.votes;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.votes.AppVote;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.AppVoteApiEvent;


public class PostAppVoteRequest extends BasePostRequest<AppVote> {
    private final String appId;

    public PostAppVoteRequest(String appId, String userId, Object body, Response.ErrorListener listener) {
        super(BASE_URL + "/apps/votes?appId=" + appId + "&userId=" + userId, null, listener);
        this.appId = appId;
    }

    @Override
    public Class<AppVote> getParsedAppClass() {
        return AppVote.class;
    }

    @Override
    public void deliverResponse(AppVote response) {
        response.setAppId(appId);
        BusProvider.getInstance().post(new AppVoteApiEvent(response, true));
    }
}
