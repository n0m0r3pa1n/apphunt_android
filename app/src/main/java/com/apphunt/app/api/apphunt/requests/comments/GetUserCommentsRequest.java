package com.apphunt.app.api.apphunt.requests.comments;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.comments.Comments;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserCommentsApiEvent;

public class GetUserCommentsRequest extends BaseGetRequest<Comments> {

    public GetUserCommentsRequest(String creatorId, String userId, Pagination pagination, Response.ErrorListener listener) {
        super(BASE_URL + "/users/"+creatorId+"/comments?userId=" + userId + "&" + pagination.getPaginationString(), listener);
    }

    public GetUserCommentsRequest(String creatorId, Pagination pagination, Response.ErrorListener listener) {
        super(BASE_URL + "/users/"+creatorId+"/comments?" + pagination.getPaginationString(), listener);
    }

    @Override
    public Class<Comments> getParsedClass() {
        return Comments.class;
    }

    @Override
    public void deliverResponse(Comments response) {
        BusProvider.getInstance().post(new GetUserCommentsApiEvent(response));
    }
}
