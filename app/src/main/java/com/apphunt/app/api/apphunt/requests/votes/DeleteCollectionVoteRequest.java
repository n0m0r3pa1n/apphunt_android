package com.apphunt.app.api.apphunt.requests.votes;

import com.android.volley.Request;
import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.votes.CollectionVote;
import com.apphunt.app.api.apphunt.requests.base.BaseGsonRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.CollectionVoteApiEvent;

/**
 * Created by nmp on 15-6-27.
 */
public class DeleteCollectionVoteRequest extends BaseGsonRequest<CollectionVote> {
    private String collectionId;
    public DeleteCollectionVoteRequest(String collectionId, String userId, Response.ErrorListener listener) {
        super(Method.DELETE, BASE_URL + "/app-collections/votes?collectionId=" + collectionId + "&userId=" + userId, listener);
        this.collectionId = collectionId;
    }

    @Override
    public Class<CollectionVote> getParsedClass() {
        return CollectionVote.class;
    }

    @Override
    public void deliverResponse(CollectionVote response) {
        response.setCollectionId(collectionId);
        BusProvider.getInstance().post(new CollectionVoteApiEvent(response, false));
    }
}
