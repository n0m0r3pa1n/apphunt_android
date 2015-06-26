package com.apphunt.app.ui.adapters;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.fragments.collections.AllCollectionsFragment;
import com.apphunt.app.ui.fragments.collections.FavouriteCollectionsFragment;
import com.apphunt.app.ui.fragments.collections.MyCollectionsFragment;
import com.apphunt.app.ui.fragments.help.AddAppFragment;
import com.apphunt.app.ui.fragments.help.TopHuntersFragment;
import com.apphunt.app.utils.ui.ActionBarUtils;

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
            case 2: fragment = new MyCollectionsFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
