package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.UserHunterStatus;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserHunterStatusApiEvent;

/**
 * Created by nmp on 16-1-27.
 */
public class GetUserHunterStatus extends BaseGetRequest<UserHunterStatus> {
    public GetUserHunterStatus(String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + userId + "/hunter-status", listener);
    }

    @Override
    public Class<UserHunterStatus> getParsedClass() {
        return UserHunterStatus.class;
    }

    @Override
    public void deliverResponse(UserHunterStatus response) {
        BusProvider.getInstance().post(new UserHunterStatusApiEvent(response));
    }
}
