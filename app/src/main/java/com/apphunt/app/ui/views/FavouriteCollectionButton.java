package com.apphunt.app.ui.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.FavouriteCollectionEvent;
import com.apphunt.app.event_bus.events.api.collections.UnfavouriteCollectionEvent;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.constants.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FavouriteCollectionButton extends LinearLayout{
    public static final String TAG = FavouriteCollectionButton.class.getSimpleName();
    private AppsCollection collection;
    private LayoutInflater inflater;

    @InjectView(R.id.favourite_button)
    ToggleButton favouriteButton;

    public FavouriteCollectionButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public FavouriteCollectionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    public FavouriteCollectionButton(Context context, AttributeSet attrs, int defStyle) {
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
        favouriteButton.setChecked(collection.isFavourite());
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

    @OnClick(R.id.favourite_button)
    public void onFavouriteButtonClicked() {
        if(!LoginProviderFactory.get((Activity) getContext()).isUserLoggedIn()) {
            LoginUtils.showLoginFragment(getContext(), false, R.string.login_info_fav_collection);
            favouriteButton.setChecked(false);
            return;
        }

        if(collection == null)
            return;

        if(!collection.isFavourite()) {
            collection.setIsFavourite(true);
            favourite();
        } else {
            collection.setIsFavourite(false);
            unfavourite();
        }
    }

    @Subscribe
    public void onFavouriteCollection(FavouriteCollectionEvent event) {
        if(!event.getCollection().getId().equals(collection.getId())){
            return;
        }

        favouriteButton.setChecked(true);
    }

    @Subscribe
    public void onUnfavouriteCollection(UnfavouriteCollectionEvent event) {
        if(!event.getCollectionId().equals(collection.getId())){
            return;
        }
        favouriteButton.setChecked(false);
    }

    private void favourite() {
        FlurryAgent.logEvent(TrackingEvents.UserFavouritedCollection);
        ApiClient.getClient(getContext()).favouriteCollection(collection,
                SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    private void unfavourite() {
        FlurryAgent.logEvent(TrackingEvents.UserUnfavouritedCollection);
        Log.d(TAG, "unfavourite " + collection.getId());
        ApiClient.getClient(getContext()).unfavouriteCollection(collection.getId(),
                SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }
}
