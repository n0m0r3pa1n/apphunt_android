package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.FollowingsList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetFollowingsApiEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetFollowingsRequest extends BaseGetRequest<FollowingsList> {

    public GetFollowingsRequest(String profileId, int page, int pageSize, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + profileId + "/following" + "?page=" + page + "&pageSize=" + pageSize, listener);
    }

    public GetFollowingsRequest(String profileId, String userId, int page, int pageSize, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + profileId + "/following" + "?page=" + page + "&pageSize=" + pageSize + "&userId=" + userId, listener);
    }

    @Override
    public Class<FollowingsList> getParsedClass() {
        return FollowingsList.class;
    }

    @Override
    public void deliverResponse(FollowingsList response) {
        BusProvider.getInstance().post(new GetFollowingsApiEvent(response));
    }
}
