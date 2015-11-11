package com.apphunt.app.ui.views.collection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ToggleButton;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.collections.FavouriteCollectionApiEvent;
import com.apphunt.app.event_bus.events.api.collections.UnfavouriteCollectionApiEvent;
import com.apphunt.app.ui.views.BaseFavouriteButton;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.InjectView;

public class FavouriteCollectionButton extends BaseFavouriteButton {
    public static final String TAG = FavouriteCollectionButton.class.getSimpleName();
    private AppsCollection collection;
    private LayoutInflater inflater;

    @InjectView(R.id.favourite_button)
    ToggleButton favouriteButton;

    public FavouriteCollectionButton(Context context) {
        super(context);
    }

    public FavouriteCollectionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavouriteCollectionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setCollection(AppsCollection collection) {
        this.collection = collection;
        favouriteButton.setChecked(collection.isFavourite());
    }

    protected boolean isDataEmpty() {
        return collection == null;
    }

    @Subscribe
    public void onFavouriteCollection(FavouriteCollectionApiEvent event) {
        if(!event.getCollection().getId().equals(collection.getId())){
            return;
        }

        favouriteButton.setChecked(true);
    }

    @Subscribe
    public void onUnfavouriteCollection(UnfavouriteCollectionApiEvent event) {
        if(!event.getCollectionId().equals(collection.getId())){
            return;
        }
        favouriteButton.setChecked(false);
    }

    @Override
    protected void favourite() {
        collection.setIsFavourite(true);
        FlurryWrapper.logEvent(TrackingEvents.UserFavouritedCollection, new HashMap<String, String>(){{
            put("collectionId", collection.getId());
        }});
        ApiClient.getClient(getContext()).favouriteCollection(collection,
                SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    @Override
    protected void unfavourite() {
        collection.setIsFavourite(false);
        FlurryWrapper.logEvent(TrackingEvents.UserUnfavouritedCollection, new HashMap<String, String>(){{
            put("collectionId", collection.getId());
        }});
        ApiClient.getClient(getContext()).unfavouriteCollection(collection.getId(),
                SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }
}
