package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.FollowersList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserFollowersApiEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetUserFollowersRequest extends BaseGetRequest<FollowersList> {

    public GetUserFollowersRequest(String userId, int page, int pageSize, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + userId + "/followers" + "?page=" + page + "&pageSize=" + pageSize, listener);
    }

    @Override
    public Class<FollowersList> getParsedClass() {
        return FollowersList.class;
    }

    @Override
    public void deliverResponse(FollowersList response) {
        BusProvider.getInstance().post(new GetUserFollowersApiEvent(response.getFollowers()));
    }
}
