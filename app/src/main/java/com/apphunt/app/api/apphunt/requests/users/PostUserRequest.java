package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserCreatedApiEvent;

public class PostUserRequest extends BasePostRequest<User>{
    public PostUserRequest(Object body, Response.ErrorListener listener) {
        super(BASE_URL + "/users", body, listener);
    }

    @Override
    public Class<User> getParsedAppClass() {
        return User.class;
    }

    @Override
    public void deliverResponse(User response) {
        BusProvider.getInstance().post(new UserCreatedApiEvent(response));
    }
}
