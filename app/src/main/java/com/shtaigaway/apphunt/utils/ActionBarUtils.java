package com.shtaigaway.apphunt.utils;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.ui.fragments.BaseFragment;

public class ActionBarUtils {

    private static ActionBarUtils instance;

    public static ActionBarUtils getInstance() {
        if (instance == null) {
            instance = new ActionBarUtils();
        }

        return instance;
    }

    private ActionBarUtils() {

    }

    public void configActionBar(Context activity) {
        FragmentManager fragmentManager = ((ActionBarActivity) activity).getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag(tag);

            ((ActionBarActivity) activity).getSupportActionBar().setTitle(fragment.getTitle());
            ((ActionBarActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((ActionBarActivity) activity).getSupportActionBar().setHomeButtonEnabled(true);
        } else if (fragmentManager.getBackStackEntryCount() == 0) {
            ((ActionBarActivity) activity).getSupportActionBar().setTitle(R.string.app_name);
            ((ActionBarActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((ActionBarActivity) activity).getSupportActionBar().setHomeButtonEnabled(false);
        }
    }
}
