package com.apphunt.app.api.apphunt.requests.votes;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.votes.CommentVote;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.CommentVoteApiEvent;


public class PostCommentVoteRequest extends BasePostRequest<CommentVote> {
    private String commentId;
    public PostCommentVoteRequest(String commentId, String userId, Response.ErrorListener listener) {
        super(BASE_URL + "/comments/votes" + "?commentId=" + commentId + "&userId=" + userId, null, listener);
        this.commentId = commentId;
    }

    @Override
    public Class<CommentVote> getParsedClass() {
        return CommentVote.class;
    }

    @Override
    public void deliverResponse(CommentVote response) {
        response.setCommentId(commentId);
        BusProvider.getInstance().post(new CommentVoteApiEvent(response, true));
    }
}
