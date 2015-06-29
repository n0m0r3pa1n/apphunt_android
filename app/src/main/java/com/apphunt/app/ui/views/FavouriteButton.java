package com.apphunt.app.ui.views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.models.votes.CollectionVote;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.votes.CollectionVoteApiEvent;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-29.
 */
public class FavouriteButton extends LinearLayout {

    private AppsCollection collection;
    private LayoutInflater inflater;

    @InjectView(R.id.favouriteButton)
    ToggleButton favouriteButton;

    public FavouriteButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public FavouriteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    public FavouriteButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init();
        }
    }

    protected void init() {
        View view = getLayoutInflater().inflate(R.layout.view_favourite, this, true);
        ButterKnife.inject(this, view);
    }

    protected LayoutInflater getLayoutInflater() {
        if(inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }

    public void setCollection(AppsCollection collection) {
        this.collection = collection;

    }

    protected void favourite() {
        FlurryAgent.logEvent(TrackingEvents.UserFavouritedCollection);
        ApiClient.getClient(getContext()).voteCollection(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                collection.getId());
    }

    protected void unfavourite() {
        FlurryAgent.logEvent(TrackingEvents.UserUnfavouritedCollection);
        ApiClient.getClient(getContext()).downVoteCollection(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                collection.getId());
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
}
