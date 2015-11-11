package com.apphunt.app.ui.views.vote;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;

import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.api.apphunt.models.votes.CommentVote;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.votes.CommentVoteApiEvent;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.squareup.otto.Subscribe;


public class CommentVoteButton extends AppVoteButton {
    private Comment comment;

    public CommentVoteButton(Context context) {
        super(context);
    }

    public CommentVoteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setComment(Comment comment) {
        this.comment = comment;
        updateVoteButton();
    }

    @Override
    protected String getButtonText() {
        return comment.getVotesCount() + "";
    }

    @Override
    protected boolean shouldBeAbleToVote() {
        return comment != null;
    }

    @Override
    protected boolean hasVoted() {
        return comment.isHasVoted();
    }

    @Override
    protected void downVote() {
        if(TextUtils.isEmpty(comment.getParent())) {
            FlurryWrapper.logEvent(TrackingEvents.UserDownVotedComment);
        } else {
            FlurryWrapper.logEvent(TrackingEvents.UserDownVotedReplyComment);
        }
        ApiClient.getClient(getContext()).downVoteComment(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), comment.getId());
    }

    @Override
    protected void vote() {
        if(TextUtils.isEmpty(comment.getParent())) {
            FlurryWrapper.logEvent(TrackingEvents.UserVotedComment);
        } else {
            FlurryWrapper.logEvent(TrackingEvents.UserVotedReplyComment);
        }
        ApiClient.getClient(getContext()).voteComment(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                comment.getId());
    }

    @Subscribe
    public void onAppCommentVoted(CommentVoteApiEvent event) {
        if(!event.getCommentVote().getCommentId().equals(comment.getId())) {
            return;
        }

        CommentVote vote = event.getCommentVote();
        comment.setHasVoted(event.isVote());
        comment.setVotesCount(vote.getVotesCount());
        voteButton.setText(String.valueOf(vote.getVotesCount()));
        updateVoteButton();

        voteButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }
}
