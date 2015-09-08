package com.apphunt.app.api.apphunt.requests;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;


public class GetNotificationRequest extends BaseGetRequest<Notification> {
    private final Response.Listener<Notification> callback;

    public GetNotificationRequest(String type, Response.Listener<Notification> callback, Response.ErrorListener listener) {
        super(BASE_URL + "/notifications?type=" + type, listener);
        this.callback = callback;
    }

    @Override
    public Class<Notification> getParsedClass() {
        return Notification.class;
    }

    @Override
    public void deliverResponse(Notification response) {
        if(callback != null) {
            callback.onResponse(response);
        }
    }
}
