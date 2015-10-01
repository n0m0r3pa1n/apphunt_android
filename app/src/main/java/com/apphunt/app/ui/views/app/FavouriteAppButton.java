package com.apphunt.app.ui.views.app;

import android.content.Context;
import android.util.AttributeSet;

import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.events.api.apps.FavouriteAppApiEvent;
import com.apphunt.app.event_bus.events.api.apps.UnfavouriteAppApiEvent;
import com.apphunt.app.ui.views.BaseFavouriteButton;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.squareup.otto.Subscribe;

 public class FavouriteAppButton extends BaseFavouriteButton {
     private BaseApp app;
     public FavouriteAppButton(Context context) {
        super(context);
    }

    public FavouriteAppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavouriteAppButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

     public void setApp(BaseApp app) {
         this.app = app;
         favouriteButton.setChecked(app.isFavourite());
     }

     @Override
    protected boolean isDataEmpty() {
        return app == null;
    }

    @Override
    protected void favourite() {
        app.setIsFavourite(true);
        ApiClient.getClient(getContext()).favouriteApp(app.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    @Override
    protected void unfavourite() {
        app.setIsFavourite(false);
        ApiClient.getClient(getContext()).unfavouriteApp(app.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
    }

    @Subscribe
    public void onFavouriteApp(FavouriteAppApiEvent event) {
        if(!event.getAppId().equals(app.getId())) {
            return;
        }

        app.setIsFavourite(true);
        favouriteButton.setChecked(true);
    }

    @Subscribe
    public void onUnfavouriteApp(UnfavouriteAppApiEvent event) {
        if(!event.getAppId().equals(app.getId())) {
            return;
        }
        app.setIsFavourite(false);
        favouriteButton.setChecked(false);
    }
 }
