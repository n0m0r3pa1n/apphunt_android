package com.apphunt.app.api.apphunt.requests.users;

import android.util.Log;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;

import org.json.JSONObject;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/21/15.
 * *
 * * NaughtySpirit 2015
 */
public class PostFollowUserRequest extends BasePostRequest<JSONObject> {

    public PostFollowUserRequest(String userId, User following, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + userId  + "/followers", following, listener);
    }

    @Override
    public Class<JSONObject> getParsedClass() {
        return JSONObject.class;
    }

    @Override
    public void deliverResponse(JSONObject response) {
        Log.e(TAG, response.toString());
    }
}
