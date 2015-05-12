package com.apphunt.app.api.apphunt.requests;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Notification;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;


public class GetNotificationRequest extends BaseGetRequest<Notification> {
    public GetNotificationRequest(String type, Response.ErrorListener listener) {
        super(BASE_URL + "/notifications?type=" + type, listener);
    }

    @Override
    public Class<Notification> getParsedAppClass() {
        return Notification.class;
    }

    @Override
    public void deliverResponse(Notification response) {

    }
}
