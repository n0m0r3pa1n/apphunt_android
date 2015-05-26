package com.apphunt.app.ui.views.vote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.api.apphunt.models.comments.CommentVote;
import com.apphunt.app.event_bus.events.api.votes.CommentVoteApiEvent;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;
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
        FlurryAgent.logEvent(TrackingEvents.UserDownVotedReplyComment);
        ApiClient.getClient(getContext()).downVoteComment(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), comment.getId());
    }

    @Override
    protected void vote() {
        FlurryAgent.logEvent(TrackingEvents.UserVotedReplyComment);
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
        if(event.isVote()) {
            voteButton.setTextColor(getResources().getColor(R.color.bg_secondary));
            voteButton.setBackgroundResource(R.drawable.btn_voted);
        } else {
            voteButton.setTextColor(getResources().getColor(R.color.bg_primary));
            voteButton.setBackgroundResource(R.drawable.btn_vote);
        }

        voteButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }
}
