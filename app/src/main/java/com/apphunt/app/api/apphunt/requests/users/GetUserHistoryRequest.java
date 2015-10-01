package com.apphunt.app.api.apphunt.requests.users;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.users.HistoryEventsList;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserHistoryApiEvent;

public class GetUserHistoryRequest extends BaseGetRequest<HistoryEventsList> {
    public GetUserHistoryRequest(String userId, String date, Response.ErrorListener listener) {
        super(BASE_URL + "/users/" + userId +"/history?date=" + date, listener);
    }

    @Override
    public Class<HistoryEventsList> getParsedClass() {
        return HistoryEventsList.class;
    }

    @Override
    public void deliverResponse(HistoryEventsList response) {
        BusProvider.getInstance().post(new GetUserHistoryApiEvent(response));
    }
}
