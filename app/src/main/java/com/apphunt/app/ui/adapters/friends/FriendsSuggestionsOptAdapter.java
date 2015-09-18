package com.apphunt.app.ui.adapters.friends;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.friends.FacebookFriends;
import com.apphunt.app.ui.fragments.friends.TwitterFriends;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/17/15.
 * *
 * * NaughtySpirit 2015
 */
public class FriendsSuggestionsOptAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = FriendsSuggestionsOptAdapter.class.getSimpleName();

    public FriendsSuggestionsOptAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;

        switch (position) {
            case 0:
                fragment = TwitterFriends.getInstance();
                break;
            case 1:
                fragment = FacebookFriends.newInstance();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
