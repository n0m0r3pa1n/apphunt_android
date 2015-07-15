package com.apphunt.app.api.apphunt.requests.votes;

import com.android.volley.Request;
import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.votes.CommentVote;
import com.apphunt.app.api.apphunt.requests.base.BaseGsonRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.CommentVoteApiEvent;


public class DeleteCommentVoteRequest extends BaseGsonRequest<CommentVote>{
    private String commentId;
    public DeleteCommentVoteRequest(String commentId, String userId, Response.ErrorListener listener) {
        super(Request.Method.DELETE, BASE_URL + "/comments/votes?commentId=" + commentId + "&userId=" + userId, listener);
        this.commentId = commentId;
    }

    @Override
    public Class<CommentVote> getParsedClass() {
        return CommentVote.class;
    }

    @Override
    public void deliverResponse(CommentVote response) {
        response.setCommentId(commentId);
        BusProvider.getInstance().post(new CommentVoteApiEvent(response, false));
    }
}
