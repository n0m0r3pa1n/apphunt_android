package com.apphunt.app.utils.ui;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.DrawerStatusEvent;
import com.apphunt.app.ui.fragments.navigation.NavigationDrawerFragment;
import com.crashlytics.android.Crashlytics;

public class ActionBarUtils {

    private static final String TAG = ActionBarUtils.class.getName();

    private static ActionBarUtils instance;
    private AppCompatActivity activity;

    public static ActionBarUtils getInstance() {
        if (instance == null) {
            instance = new ActionBarUtils();
        }

        return instance;
    }

    private ActionBarUtils() {
    }

    public void init(AppCompatActivity activity) {
        this.activity = activity;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            activity.findViewById(R.id.shadow).setVisibility(View.VISIBLE);
//        } else {
//            activity.findViewById(R.id.shadow).setVisibility(View.GONE);
//        }

        configActionBar(activity);
    }

    public void configActionBar(Context ctx) {
        AppCompatActivity activity = ((AppCompatActivity) ctx);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (fragmentManager.getBackStackEntryCount() > 0) {
            NavigationDrawerFragment.setDrawerIndicatorEnabled(true);
            activity.getSupportActionBar().collapseActionView();
            BusProvider.getInstance().post(new DrawerStatusEvent(true));
        } else if (fragmentManager.getBackStackEntryCount() == 0) {
            NavigationDrawerFragment.setDrawerIndicatorEnabled(false);
            BusProvider.getInstance().post(new DrawerStatusEvent(false));
        }

        activity.supportInvalidateOptionsMenu();
    }

    public void setTitle(String title) {
        activity.getSupportActionBar().setTitle(title);
    }

    public void setSubtitle(String title) {
        activity.getSupportActionBar().setSubtitle(title);
    }

    public void setTitle(int titleRes) {
        try {
            setTitle(activity.getResources().getString(titleRes));
        } catch (Exception e) {
            Crashlytics.getInstance().core.logException(e);
        }
    }

    public void showActionBar(AppCompatActivity activity) {
        activity.getSupportActionBar().show();
    }

    public void hideActionBar(AppCompatActivity activity) {
        activity.getSupportActionBar().hide();
    }

    public void hideActionBarShadow() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            activity.findViewById(R.id.shadow).setVisibility(View.GONE);
//        } else {
//            activity.findViewById(R.id.toolbar).setElevation(R.dimen.zero_elevation);
//        }
    }

    public void showActionBarShadow() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            activity.findViewById(R.id.shadow).setVisibility(View.VISIBLE);
//        } else {
//            activity.findViewById(R.id.toolbar).setElevation(activity.getResources().getDimension(R.dimen.one_elevation));
//        }
    }

    public void invalidateOptionsMenu() {
        activity.supportInvalidateOptionsMenu();
    }
}
