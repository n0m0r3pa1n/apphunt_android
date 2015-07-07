package com.apphunt.app.ui.views.vote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.models.votes.CollectionVote;
import com.apphunt.app.event_bus.events.api.votes.CollectionVoteApiEvent;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.constants.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

/**
 * Created by nmp on 15-6-26.
 */
public class CollectionVoteButton extends AppVoteButton {
    private AppsCollection collection;

    public CollectionVoteButton(Context context) {
        super(context);
    }

    public CollectionVoteButton(Context context, App baseApp) {
        super(context, baseApp);
    }

    public CollectionVoteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollectionVoteButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCollection(AppsCollection appsCollection) {
        this.collection = appsCollection;
        updateVoteButton();
    }

    @Override
    protected boolean hasVoted() {
        return collection.hasUserVoted();
    }

    @Override
    protected boolean shouldBeAbleToVote() {
        return collection != null;
    }

    @Override
    protected String getButtonText() {
        return collection.getVotesCount() + "";
    }

    @Override
    protected void vote() {
        FlurryAgent.logEvent(TrackingEvents.UserVotedCollection);
        ApiClient.getClient(getContext()).voteCollection(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                collection.getId());
    }

    @Override
    protected void downVote() {
        FlurryAgent.logEvent(TrackingEvents.UserDownVotedCollection);
        ApiClient.getClient(getContext()).downVoteCollection(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                collection.getId());
    }

    @Subscribe
    public void onAppCommentVoted(CollectionVoteApiEvent event) {
        if(!event.getCollectionVote().getCollectionId().equals(collection.getId())) {
            return;
        }

        CollectionVote vote = event.getCollectionVote();
        collection.setHasVoted(event.isVote());
        collection.setVotesCount(vote.getVotesCount());
        voteButton.setText(String.valueOf(vote.getVotesCount()));
        updateVoteButton();

        voteButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }
}
