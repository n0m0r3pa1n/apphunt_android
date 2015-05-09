package com.apphunt.app.api.apphunt.requests.comments;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Comments;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.LoadAppCommentsApiEvent;


public class GetAppCommentsRequest extends BaseGetRequest<Comments> {
    private boolean shouldReload;
    public GetAppCommentsRequest(String appId, String userId, int page, int pageSize, boolean shouldReload,
                                 Response.ErrorListener listener) {
        super(BASE_URL + "/comments/" + appId + "?userId=" + userId + "&page=" + page + "&pageSize=" +  pageSize, listener);
        this.shouldReload = shouldReload;
    }

    @Override
    public Class<Comments> getParsedAppClass() {
        return Comments.class;
    }

    @Override
    public void deliverResponse(Comments response) {
        BusProvider.getInstance().post(new LoadAppCommentsApiEvent(response, shouldReload));
    }
}
