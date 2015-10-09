package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserFollowApiEvent;

import org.json.JSONObject;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/21/15.
 * *
 * * NaughtySpirit 2015
 */
public class PostFollowUserRequest extends BasePostRequest<JSONObject> {
    private String followingId;

    public PostFollowUserRequest(String userId, String followingId, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + followingId  + "/followers/" + userId, null, listener);
        this.followingId = followingId;
    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        BusProvider.getInstance().post(new UserFollowApiEvent(followingId, true));
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        BusProvider.getInstance().post(new UserFollowApiEvent(followingId, false));
    }
}
