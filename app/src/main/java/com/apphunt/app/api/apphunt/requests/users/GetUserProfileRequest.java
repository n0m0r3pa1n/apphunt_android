package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.UserProfile;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserProfileApiEvent;

public class GetUserProfileRequest extends BaseGetRequest<UserProfile> {
    public GetUserProfileRequest(String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + userId, listener);
    }

    @Override
    public Class<UserProfile> getParsedClass() {
        return UserProfile.class;
    }

    @Override
    public void deliverResponse(UserProfile response) {
        BusProvider.getInstance().post(new GetUserProfileApiEvent(response));
    }
}
