package com.apphunt.app.ui.adapters.friends;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.friends.FacebookFriends;
import com.apphunt.app.ui.fragments.friends.TwitterFriends;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/17/15.
 * *
 * * NaughtySpirit 2015
 */
public class FriendsSuggestionsOptAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = FriendsSuggestionsOptAdapter.class.getSimpleName();

    private ArrayList<BaseFragment> fragments = new ArrayList<>();

    public FriendsSuggestionsOptAdapter(FragmentManager fm) {
        super(fm);
    }

    public void instantiateFragments() {
        fragments.add(TwitterFriends.getInstance());
        fragments.add(FacebookFriends.getInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void removeFragments() {
        BaseFragment twitter = fragments.get(0);
        twitter = null;

        BaseFragment facebook = fragments.get(1);
        facebook = null;

        fragments.clear();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
