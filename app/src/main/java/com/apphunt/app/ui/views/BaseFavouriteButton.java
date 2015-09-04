package com.apphunt.app.ui.views;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.ui.views.collection.FavouriteCollectionButton;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public abstract class BaseFavouriteButton extends LinearLayout {
    public static final String TAG = FavouriteCollectionButton.class.getSimpleName();
    private LayoutInflater inflater;

    @InjectView(R.id.favourite_button)
    public ToggleButton favouriteButton;
    private AppCompatActivity activity;

    public BaseFavouriteButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public BaseFavouriteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    public BaseFavouriteButton(Context context, AttributeSet attrs, int defStyle) {
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
        if(TextUtils.isEmpty(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID))) {
            LoginUtils.showLoginFragment(activity, false, R.string.login_info_fav_collection);
            favouriteButton.setChecked(false);
            return;
        }

        if (isDataEmpty()) return;

        if(favouriteButton.isChecked()) {
            favourite();
        } else {
            unfavourite();
        }
    }

    protected abstract boolean isDataEmpty();

    protected abstract void favourite();

    protected abstract void unfavourite();

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }
}
