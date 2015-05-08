package com.apphunt.app.ui.views.vote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.callback.Callback;
import com.apphunt.app.api.apphunt.models.Comment;
import com.apphunt.app.api.apphunt.models.CommentVote;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;

import retrofit.client.Response;

/**
 * Created by nmp on 15-5-8.
 */
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
        ApiClient.getClient(getContext()).downVoteComment(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), comment.getId(),
                new Callback<CommentVote>() {
                    @Override
                    public void success(CommentVote vote, Response response) {
                        comment.setHasVoted(false);
                        comment.setVotesCount(vote.getVotesCount());
                        voteButton.setTextColor(getResources().getColor(R.color.bg_primary));
                        voteButton.setText(String.valueOf(vote.getVotesCount()));
                        voteButton.setBackgroundResource(R.drawable.btn_vote);
                        voteButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    }
                });
    }

    @Override
    protected void vote() {
        FlurryAgent.logEvent(TrackingEvents.UserVotedReplyComment);
        ApiClient.getClient(getContext()).voteComment(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), comment.getId(), new Callback<CommentVote>() {
            @Override
            public void success(CommentVote vote, Response response) {
                comment.setHasVoted(true);
                comment.setVotesCount(vote.getVotesCount());
                voteButton.setTextColor(getResources().getColor(R.color.bg_secondary));
                voteButton.setText(String.valueOf(vote.getVotesCount()));
                voteButton.setBackgroundResource(R.drawable.btn_voted);
                voteButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });
    }


}
