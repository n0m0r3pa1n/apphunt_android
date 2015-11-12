package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;

public class PostUserRequest extends BasePostRequest<User>{

    private final Response.Listener<User> responseListener;

    public PostUserRequest(Object body, Response.Listener<User> responseListener, Response.ErrorListener listener) {
        super(BASE_URL + "/users", body, listener);
        this.responseListener = responseListener;
    }

    @Override
    public Class<User> getParsedClass() {
        return User.class;
    }

    @Override
    public void deliverResponse(User response) {
        if(responseListener != null) {
            responseListener.onResponse(response);
        }
    }
}
