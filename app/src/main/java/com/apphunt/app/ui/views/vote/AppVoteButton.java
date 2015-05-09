package com.apphunt.app.ui.views.vote;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.callback.Callback;
import com.apphunt.app.api.apphunt.models.App;
import com.apphunt.app.api.apphunt.models.Vote;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.VoteForAppEvent;
import com.apphunt.app.event_bus.events.ui.votes.AppVoteEvent;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.client.Response;

/**
 * Created by nmp on 15-5-8.
 */
public class AppVoteButton extends LinearLayout {
    public static final String TAG = AppVoteButton.class.getSimpleName();
    private App app;
    private LayoutInflater inflater;

    @InjectView(R.id.vote)
    Button voteButton;

    public AppVoteButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public AppVoteButton(Context context, App app) {
        super(context);
        this.app = app;
        if (!isInEditMode()) {
            init(context);
        }
    }

    public AppVoteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public AppVoteButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init(context);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BusProvider.getInstance().unregister(this);
    }

    protected LayoutInflater getLayoutInflater() {
        if(inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }

    protected void init(Context context) {
        View view = getLayoutInflater().inflate(R.layout.view_vote_button, this, true);
        ButterKnife.inject(this, view);
        if(!shouldBeAbleToVote()) {
            voteButton.setText("0");
            return;
        }

        updateVoteButton();
    }

    protected void updateVoteButton() {
        if (hasVoted()) {
            voteButton.setTextColor(getResources().getColor(R.color.bg_secondary));
            voteButton.setBackgroundResource(R.drawable.btn_voted_v2);
        } else {
            voteButton.setTextColor(getResources().getColor(R.color.bg_primary));
            voteButton.setBackgroundResource(R.drawable.btn_vote);
        }
        voteButton.setText(getButtonText());
    }

    protected String getButtonText() {
        return app.getVotesCount();
    }

    public void setApp(App app) {
        this.app = app;
        updateVoteButton();
    }

    @OnClick(R.id.vote)
    public void vote(View view) {
        if(!LoginProviderFactory.get((Activity)getContext()).isUserLoggedIn()) {
            LoginUtils.showLoginFragment(getContext());
            return;
        }

        if(!shouldBeAbleToVote())
            return;

        if(hasVoted()) {
            downVote();
        } else {
            vote();
        }
    }

    protected boolean shouldBeAbleToVote() {
        return app != null;
    }

    protected boolean hasVoted() {
        return app.isHasVoted();
    }

    protected void downVote() {
        ApiClient.getClient(getContext()).downVote(app.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), new Callback<Vote>() {
            @Override
            public void success(Vote voteResult, Response response) {
                FlurryAgent.logEvent(TrackingEvents.UserDownVotedAppFromDetails);
                app.setVotesCount(voteResult.getVotes());
                app.setHasVoted(false);
                voteButton.setText(voteResult.getVotes());
                voteButton.setTextColor(getResources().getColor(R.color.bg_primary));
                voteButton.setBackgroundResource(R.drawable.btn_vote);
                postUserVotedEvent(false);
            }
        });
    }

    protected void vote() {
        ApiClient.getClient(getContext()).vote(app.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    @Subscribe
    public void onAppVote(VoteForAppEvent event) {
        Vote voteResult = event.getVote();
        FlurryAgent.logEvent(TrackingEvents.UserVotedAppFromDetails);
        app.setVotesCount(voteResult.getVotes());
        app.setHasVoted(true);
        voteButton.setText(voteResult.getVotes());
        voteButton.setBackgroundResource(R.drawable.btn_voted_v2);
        voteButton.setTextColor(getResources().getColor(R.color.bg_secondary));
        postUserVotedEvent(true);
    }

    protected void postUserVotedEvent(boolean hasVoted) {
        BusProvider.getInstance().post(new AppVoteEvent(hasVoted, app.getPosition()));
    }


}
