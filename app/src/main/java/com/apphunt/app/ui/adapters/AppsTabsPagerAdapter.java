package com.apphunt.app.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.apphunt.app.ui.fragments.DailyAppsFragment;
import com.apphunt.app.ui.fragments.TrendingAppsFragment;
import com.apphunt.app.ui.fragments.base.BaseFragment;

/**
 * Created by nmp on 15-12-21.
 */
public class AppsTabsPagerAdapter extends FragmentStatePagerAdapter {
    public AppsTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;
        switch (position) {
            case 0: fragment = new DailyAppsFragment();
                break;
            case 1: fragment = TrendingAppsFragment.newInstance();
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
                return "Daily";
            case 1:
                return "Trending";
            default:
                return "Daily";
        }
    }
}
