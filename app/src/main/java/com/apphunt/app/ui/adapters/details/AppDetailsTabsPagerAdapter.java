package com.apphunt.app.ui.adapters.details;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.details.GalleryDetailsFragment;
import com.apphunt.app.ui.fragments.details.ReviewsFragment;

public class AppDetailsTabsPagerAdapter extends FragmentStatePagerAdapter {

    public AppDetailsTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;
        switch (position) {
            case 0: fragment = new ReviewsFragment();
                break;
            case 1: fragment = new GalleryDetailsFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Comments";
            case 1:
                return "Gallery";
            default:
                return "Comments";
        }
    }
}

