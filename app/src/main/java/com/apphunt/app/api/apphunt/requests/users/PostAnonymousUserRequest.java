package com.apphunt.app.api.apphunt.requests.users;

import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.AnonymousUserCreatedApiEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 11/3/15.
 * *
 * * NaughtySpirit 2015
 */
public class PostAnonymousUserRequest extends BasePostRequest<User> {

    public PostAnonymousUserRequest(Object body, com.android.volley.Response.ErrorListener listener) {
        super(BASE_URL + "/users", body, listener);
    }

    @Override
    public Class<User> getParsedClass() {
        return User.class;
    }

    @Override
    public void deliverResponse(User response) {
        BusProvider.getInstance().post(new AnonymousUserCreatedApiEvent(response));
    }
}
