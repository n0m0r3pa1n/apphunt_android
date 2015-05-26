package com.apphunt.app.utils.ui;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.SaveAppFragment;
import com.apphunt.app.ui.fragments.SelectAppFragment;
import com.apphunt.app.ui.fragments.TopAppsFragment;
import com.apphunt.app.utils.Constants;

/**
 * Created by nmp on 15-5-9.
 */
public class NavUtils {

    private static NavUtils instance;
    private ActionBarActivity activity;
    private boolean isBlocked = false;

    public static NavUtils getInstance(ActionBarActivity activity) {
        if (instance == null) {
            instance = new NavUtils(activity);
        }

        return instance;
    }

    private NavUtils(ActionBarActivity activity) {
        this.activity = activity;
    }

    public void startSelectAppFragment() {
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, new SelectAppFragment(), Constants.TAG_SELECT_APP_FRAGMENT)
                .addToBackStack(Constants.TAG_SELECT_APP_FRAGMENT)
                .commit();

        activity.getSupportFragmentManager().executePendingTransactions();
    }

    public void startSaveAppFragment(ApplicationInfo data) {
        String curFragmentTag = activity.getSupportFragmentManager().getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

        if (!curFragmentTag.equals(Constants.TAG_SAVE_APP_FRAGMENT)) {
            Bundle extras = new Bundle();
            extras.putParcelable(Constants.KEY_DATA, data);

            SaveAppFragment saveAppFragment = new SaveAppFragment();
            saveAppFragment.setArguments(extras);

            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .add(R.id.container, saveAppFragment, Constants.TAG_SAVE_APP_FRAGMENT)
                    .addToBackStack(Constants.TAG_SAVE_APP_FRAGMENT)
                    .commit();
        }
    }

    public void startTopAppsFragment() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new TopAppsFragment())
                .commit();
    }

    public void setOnBackBlocked(boolean isBlocked) {
        if (isBlocked) {
            ActionBarUtils.getInstance().hideActionBar(activity);
        } else {
            ActionBarUtils.getInstance().showActionBar(activity);
        }
        this.isBlocked = isBlocked;
    }

    public boolean isOnBackBlocked() {
        return isBlocked;
    }
}
