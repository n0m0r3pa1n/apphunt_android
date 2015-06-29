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
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.votes.AppVote;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.AppVoteApiEvent;
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

public class AppVoteButton extends LinearLayout {
    public static final String TAG = AppVoteButton.class.getSimpleName();
    private App baseApp;
    private LayoutInflater inflater;

    @InjectView(R.id.vote)
    Button voteButton;

    public AppVoteButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public AppVoteButton(Context context, App baseApp) {
        super(context);
        this.baseApp = baseApp;
        if (!isInEditMode()) {
            init();
        }
    }

    public AppVoteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    public AppVoteButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init();
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

    protected void init() {
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
        return baseApp.getVotesCount();
    }

    public void setBaseApp(App baseApp) {
        this.baseApp = baseApp;
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
        return baseApp != null;
    }

    protected boolean hasVoted() {
        return baseApp.isHasVoted();
    }

    protected void downVote() {
        ApiClient.getClient(getContext()).downVote(baseApp.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    protected void vote() {
        ApiClient.getClient(getContext()).vote(baseApp.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    @Subscribe
    public void onAppVote(AppVoteApiEvent event) {
        if(!baseApp.getId().equals(event.getVote().getAppId())) {
            return;
        }
        AppVote voteResult = event.getVote();
        baseApp.setVotesCount(voteResult.getVotes());
        baseApp.setHasVoted(event.isVote());
        voteButton.setText(voteResult.getVotes());
        updateVoteButton();

        postUserVotedEvent(event.isVote());
    }

    protected void postUserVotedEvent(boolean hasVoted) {
        BusProvider.getInstance().post(new AppVoteEvent(hasVoted, baseApp.getPosition()));
    }


}
