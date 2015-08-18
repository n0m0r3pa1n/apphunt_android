package com.apphunt.app.ui.adapters.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.collections.tabs.AllCollectionsFragment;
import com.apphunt.app.ui.fragments.collections.tabs.FavouriteCollectionsFragment;
import com.apphunt.app.ui.fragments.collections.tabs.MyCollectionsFragment;

public class ProfileTabsPagerAdapter extends FragmentStatePagerAdapter {
    public ProfileTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;
        switch (position) {
            case 0: fragment = AllCollectionsFragment.newInstance();
                break;
            case 1: fragment = FavouriteCollectionsFragment.newInstance();
                break;
            case 2:
                fragment = MyCollectionsFragment.newInstance();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Apps";
            case 1:
                return "Collections";
            case 2:
                return "Comments";
            default:
                return "Item";
        }
    }
}

