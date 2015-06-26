package com.apphunt.app.utils.ui;

import android.support.v4.app.FragmentActivity;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.ui.fragments.collection_creator.CreateCollectionFragment;
import com.apphunt.app.ui.fragments.collection_creator.SelectCollectionFragment;
import com.apphunt.app.utils.Constants;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 6/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class AddToCollectionUtils {

    private static AddToCollectionUtils instance;

    public static AddToCollectionUtils getInstance() {
        if (instance == null) {
            instance = new AddToCollectionUtils();
        }

        return instance;
    }

    private AddToCollectionUtils() {}

    public void presentSelectCollectionFragment(FragmentActivity activity, App app) {
        SelectCollectionFragment fragment = new SelectCollectionFragment();
        fragment.setSelectedApp(app);

        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, Constants.TAG_SELECT_COLLECTION_FRAGMENT)
                .addToBackStack(Constants.TAG_SELECT_COLLECTION_FRAGMENT)
                .commit();
    }

    public void presentCreateCollectionFragment(FragmentActivity activity) {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new CreateCollectionFragment(), Constants.TAG_CREATE_COLLECTION_FRAGMENT)
                .addToBackStack(Constants.TAG_CREATE_COLLECTION_FRAGMENT)
                .commit();
    }
}
