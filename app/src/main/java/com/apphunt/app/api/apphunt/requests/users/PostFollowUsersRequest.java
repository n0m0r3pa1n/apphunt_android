package com.apphunt.app.api.apphunt.requests.users;

import android.util.Log;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.FollowingsList;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;

import org.json.JSONObject;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/22/15.
 * *
 * * NaughtySpirit 2015
 */
public class PostFollowUsersRequest extends BasePostRequest<JSONObject> {

    public PostFollowUsersRequest(String userId, FollowingsList followingIds, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + userId + "/following", followingIds, listener);
    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        Log.e(TAG, response.toString());
        // TODO: Bus event for follow users response
    }
}
