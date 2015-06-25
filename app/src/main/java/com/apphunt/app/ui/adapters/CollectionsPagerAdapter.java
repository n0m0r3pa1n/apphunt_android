package com.apphunt.app.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.apphunt.app.ui.fragments.help.AddAppFragment;
import com.apphunt.app.ui.fragments.help.AppsRequirementsFragment;
import com.apphunt.app.ui.fragments.help.TopHuntersFragment;

/**
 * Created by nmp on 15-6-25.
 */
public class CollectionsPagerAdapter extends FragmentStatePagerAdapter {
    public CollectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Log.d("AAAAAAAAA", " " + position);
        switch (position) {
            case 0: fragment = new AppsRequirementsFragment();
                break;
            case 1: fragment = new AddAppFragment();
                break;
            case 2: fragment = new TopHuntersFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
