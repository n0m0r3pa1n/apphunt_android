package com.apphunt.app.api.apphunt.requests.comments;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.NewComment;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.ReloadCommentsEvent;

/**
 * Created by nmp on 15-5-9.
 */
public class PostNewCommentRequest extends BasePostRequest<NewComment> {
    public PostNewCommentRequest(NewComment body, Response.ErrorListener listener) {
        super(BASE_URL + "/comments", body, listener);
    }

    @Override
    public Class<NewComment> getParsedAppClass() {
        return NewComment.class;
    }

    @Override
    public void deliverResponse(NewComment response) {
        BusProvider.getInstance().post(new ReloadCommentsEvent());
    }
}
