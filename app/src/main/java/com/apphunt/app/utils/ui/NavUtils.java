package com.apphunt.app.utils.ui;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.fragments.AppDetailsFragment;
import com.apphunt.app.ui.fragments.SaveAppFragment;
import com.apphunt.app.ui.fragments.SelectAppFragment;
import com.apphunt.app.ui.fragments.collections.ViewCollectionFragment;
import com.apphunt.app.ui.fragments.collections.CreateCollectionFragment;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.ui.fragments.collections.SelectCollectionFragment;

/**
 * Created by nmp on 15-5-9.
 */
public class NavUtils {

    private static NavUtils instance;
    private AppCompatActivity activity;
    private boolean isBlocked = false;

    public static NavUtils getInstance(AppCompatActivity activity) {
        if (instance == null) {
            instance = new NavUtils(activity);
        }

        return instance;
    }

    private NavUtils(AppCompatActivity activity) {
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

    public void presentSelectCollectionFragment(App app) {
        SelectCollectionFragment fragment = SelectCollectionFragment.newInstance(app);

        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, Constants.TAG_SELECT_COLLECTION_FRAGMENT)
                .addToBackStack(Constants.TAG_SELECT_COLLECTION_FRAGMENT)
                .commit();
    }

    public void presentCreateCollectionFragment() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new CreateCollectionFragment(), Constants.TAG_CREATE_COLLECTION_FRAGMENT)
                .addToBackStack(Constants.TAG_CREATE_COLLECTION_FRAGMENT)
                .commit();
    }

    public void setOnBackBlocked(boolean isBlocked) {
        if (isBlocked) {
            ActionBarUtils.getInstance().hideActionBar((ActionBarActivity) activity);
        } else {
            ActionBarUtils.getInstance().showActionBar((ActionBarActivity) activity);
        }
        this.isBlocked = isBlocked;
    }

    public void presentViewCollectionFragment(AppsCollection appsCollection) {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, ViewCollectionFragment.newInstance(appsCollection),
                        Constants.TAG_COLLECTION_DETAILS)
                .addToBackStack("AAA")
                .commit();
    }

    public void presentAppDetailsFragment(BaseApp app) {
        AppDetailsFragment detailsFragment = new AppDetailsFragment();
        detailsFragment.setPreviousTitle(activity.getString(R.string.title_home));

        Bundle extras = new Bundle();
        extras.putString(Constants.KEY_APP_ID, app.getId());
        extras.putString(Constants.KEY_APP_NAME, app.getName());
        detailsFragment.setArguments(extras);

        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                .add(R.id.container, detailsFragment, Constants.TAG_APP_DETAILS_FRAGMENT)
                .addToBackStack(Constants.TAG_APP_DETAILS_FRAGMENT)
                .commit();
    }

    public boolean isOnBackBlocked() {
        return isBlocked;
    }
}
