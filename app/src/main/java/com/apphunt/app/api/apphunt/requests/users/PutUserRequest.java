package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserUpdatedApiEvent;

public class PutUserRequest extends BasePostRequest<User>{
    public PutUserRequest(User user, Response.ErrorListener listener) {
        super(BASE_URL + "/users?userId=" + user.getId(), user, listener);
    }

    @Override
    public Class<User> getParsedAppClass() {
        return User.class;
    }

    @Override
    public void deliverResponse(User response) {
        BusProvider.getInstance().post(new UserUpdatedApiEvent(response));
    }
}
