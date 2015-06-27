package com.apphunt.app.api.apphunt.requests.votes;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.votes.AppVote;
import com.apphunt.app.api.apphunt.models.votes.CollectionVote;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.AppVoteApiEvent;
import com.apphunt.app.event_bus.events.api.votes.CollectionVoteApiEvent;

/**
 * Created by nmp on 15-6-27.
 */
public class PostCollectionVoteRequest extends BasePostRequest<CollectionVote> {
    private final String collectionId;

    public PostCollectionVoteRequest(String collectionId, String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/votes?collectionId=" + collectionId + "&userId=" + userId, null, listener);
        this.collectionId = collectionId;
    }

    @Override
    public Class<CollectionVote> getParsedAppClass() {
        return CollectionVote.class;
    }

    @Override
    public void deliverResponse(CollectionVote response) {
        response.setCollectionId(collectionId);
        BusProvider.getInstance().post(new CollectionVoteApiEvent(response, true));
    }
}
