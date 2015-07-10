package com.apphunt.app.ui.adapters.collections;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.fragments.collections.tabs.AllCollectionsFragment;
import com.apphunt.app.ui.fragments.collections.tabs.FavouriteCollectionsFragment;
import com.apphunt.app.ui.fragments.collections.tabs.MyCollectionsFragment;

/**
 * Created by nmp on 15-6-25.
 */
public class CollectionsPagerAdapter extends FragmentStatePagerAdapter {
    public CollectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;
        switch (position) {
            case 0: fragment = new AllCollectionsFragment();
                break;
            case 1: fragment = new FavouriteCollectionsFragment();
                break;
            case 2:
                fragment = new MyCollectionsFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
