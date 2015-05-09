package com.apphunt.app.utils.ui;

import android.support.v7.app.ActionBarActivity;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.SelectAppFragment;
import com.apphunt.app.utils.Constants;

/**
 * Created by nmp on 15-5-9.
 */
public class NavUtils {
    public static void startSelectAppFragment(ActionBarActivity activity) {
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, new SelectAppFragment(), Constants.TAG_SELECT_APP_FRAGMENT)
                .addToBackStack(Constants.TAG_SELECT_APP_FRAGMENT)
                .commit();

        activity.getSupportFragmentManager().executePendingTransactions();
    }
}
