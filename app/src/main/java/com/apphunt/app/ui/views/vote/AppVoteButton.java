package com.apphunt.app.ui.views.vote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.votes.AppVote;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.AppVoteApiEvent;
import com.apphunt.app.event_bus.events.ui.votes.AppVoteEvent;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AppVoteButton extends LinearLayout {
    public static final String TAG = AppVoteButton.class.getSimpleName();
    private BaseApp baseApp;
    private LayoutInflater inflater;
    private String screen = "";

    @InjectView(R.id.vote)
    TextView voteButton;

    public AppVoteButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(null);
        }
    }

    public AppVoteButton(Context context, App baseApp) {
        super(context);
        this.baseApp = baseApp;
        if (!isInEditMode()) {
            init(null);
        }
    }

    public AppVoteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    public AppVoteButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            BusProvider.getInstance().register(this);
        } catch(Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            BusProvider.getInstance().unregister(this);
        } catch(Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    protected LayoutInflater getLayoutInflater() {
        if(inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }

    protected void init(AttributeSet attrs) {
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
            voteButton.setBackgroundResource(R.drawable.btn_voted);
        } else {
            voteButton.setTextColor(getResources().getColor(R.color.bg_primary));
            voteButton.setBackgroundResource(R.drawable.btn_vote);
        }
        voteButton.setText(getButtonText());
    }

    protected String getButtonText() {
        return baseApp.getVotesCount();
    }

    public void setApp(BaseApp baseApp) {
        this.baseApp = baseApp;
        updateVoteButton();
    }

    @OnClick(R.id.vote)
    public void vote(View view) {
        if(!shouldBeAbleToVote())
            return;

        if(hasVoted()) {
            downVote();
        } else {
            vote();
        }
    }

    public void setTrackingScreen(String screen) {
        this.screen = screen;
    }

    protected boolean shouldBeAbleToVote() {
        return baseApp != null;
    }

    protected boolean hasVoted() {
        return baseApp.hasVoted();
    }

    protected void downVote() {
        FlurryWrapper.logEvent(TrackingEvents.UserDownVotedApp, new HashMap<String, String>(){{
            put("screen", screen);
        }});
        ApiClient.getClient(getContext()).downVote(baseApp.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    protected void vote() {
        FlurryWrapper.logEvent(TrackingEvents.UserVotedApp, new HashMap<String, String>(){{
            put("screen", screen);
        }});
        ApiClient.getClient(getContext()).vote(baseApp.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    @Subscribe
    public void onAppVote(AppVoteApiEvent event) {
        if(baseApp == null || !baseApp.getId().equals(event.getVote().getAppId())) {
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
        BusProvider.getInstance().post(new AppVoteEvent(hasVoted));
    }


}
