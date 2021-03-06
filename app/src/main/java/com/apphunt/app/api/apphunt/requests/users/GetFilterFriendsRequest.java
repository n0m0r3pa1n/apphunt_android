package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.NamesList;
import com.apphunt.app.api.apphunt.models.users.UsersList;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.constants.Constants.LoginProviders;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetFilterUsersApiEvent;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/19/15.
 * *
 * * NaughtySpirit 2015
 */
public class GetFilterFriendsRequest extends BasePostRequest<UsersList> {

    private final LoginProviders provider;

    public GetFilterFriendsRequest(String userId, NamesList names, LoginProviders provider, Response.ErrorListener listener) {
        super(BASE_URL + "/users/actions/filter?userId=" + userId, names, listener);
        this.provider = provider;
    }

    @Override
    public Class<UsersList> getParsedClass() {
        return UsersList.class;
    }

    @Override
    public void deliverResponse(UsersList response) {
        BusProvider.getInstance().post(new GetFilterUsersApiEvent(response, provider));
    }
}
