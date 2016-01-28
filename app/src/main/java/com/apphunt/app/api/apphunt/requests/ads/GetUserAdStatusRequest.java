package com.apphunt.app.api.apphunt.requests.ads;

import com.android.volley.Request;
import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.ads.UserAdStatus;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ads.UserAdStatusApiEvent;

/**
 * Created by nmp on 16-1-20.
 */
public class GetUserAdStatusRequest extends BaseGetRequest<UserAdStatus> {
    public GetUserAdStatusRequest(String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + userId + "/ads/status", listener);
    }

    @Override
    public Class<UserAdStatus> getParsedClass() {
        return UserAdStatus.class;
    }

    @Override
    public void deliverResponse(UserAdStatus response) {
        BusProvider.getInstance().post(new UserAdStatusApiEvent(response));
    }
}
