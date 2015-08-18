package com.apphunt.app.ui.adapters.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.collections.tabs.MyCollectionsFragment;
import com.apphunt.app.ui.fragments.profile.tabs.AppsFragment;
import com.apphunt.app.ui.fragments.profile.tabs.CommentsFragment;

public class ProfileTabsPagerAdapter extends FragmentStatePagerAdapter {
    private String userId;
    public ProfileTabsPagerAdapter(FragmentManager fm, String userId) {
        super(fm);
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;
        switch (position) {
            case 0: fragment = AppsFragment.newInstance(userId);
                break;
            case 1: fragment = MyCollectionsFragment.newInstance(userId);
                break;
            case 2:
                fragment = CommentsFragment.newInstance(userId);
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

