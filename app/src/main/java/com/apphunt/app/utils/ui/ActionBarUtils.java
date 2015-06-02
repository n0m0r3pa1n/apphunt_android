package com.apphunt.app.utils.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.apphunt.app.R;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.DrawerStatusEvent;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.fragments.navigation.NavigationDrawerFragment;

public class ActionBarUtils {

    private static final String TAG = ActionBarUtils.class.getName();

    private static ActionBarUtils instance;
    private Activity activity;

    public static ActionBarUtils getInstance() {
        if (instance == null) {
            instance = new ActionBarUtils();
        }

        return instance;
    }

    private ActionBarUtils() {
    }

    public void init(ActionBarActivity activity) {
        this.activity = activity;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.findViewById(R.id.shadow).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.shadow).setVisibility(View.GONE);
        }

        configActionBar(activity);
    }

    public void configActionBar(Context activity) {
        ActionBarActivity actionBarActivity = ((ActionBarActivity) activity);
        FragmentManager fragmentManager = actionBarActivity.getSupportFragmentManager();
        actionBarActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (fragmentManager.getBackStackEntryCount() > 0) {
            String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(tag);
            NavigationDrawerFragment.setDrawerIndicatorEnabled(true);
            actionBarActivity.getSupportActionBar().setTitle(fragment.getTitle());
            actionBarActivity.getSupportActionBar().collapseActionView();
            BusProvider.getInstance().post(new DrawerStatusEvent(true));
        } else if (fragmentManager.getBackStackEntryCount() == 0) {
            actionBarActivity.getSupportActionBar().setTitle(R.string.app_name);
            NavigationDrawerFragment.setDrawerIndicatorEnabled(false);
            BusProvider.getInstance().post(new DrawerStatusEvent(false));
        }

        actionBarActivity.supportInvalidateOptionsMenu();
    }

    public void showActionBar(ActionBarActivity activity) {
        activity.getSupportActionBar().show();
    }

    public void hideActionBar(ActionBarActivity activity) {
        activity.getSupportActionBar().hide();
    }

    public void hideActionBarShadow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.findViewById(R.id.shadow).setVisibility(View.GONE);
        } else {
            ((ActionBarActivity) activity).getSupportActionBar().setElevation(R.dimen.zero_elevation);
        }
    }

    public void showActionBarShadow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.findViewById(R.id.shadow).setVisibility(View.VISIBLE);
        } else {
            ((ActionBarActivity) activity).getSupportActionBar().setElevation(activity.getResources().getDimension(R.dimen.one_elevation));
        }
    }
}
