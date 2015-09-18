package com.apphunt.app.ui.fragments.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.apphunt.app.R;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.adapters.friends.FriendsSuggestionsOptAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.flurry.android.FlurryAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/15/15.
 * *
 * * NaughtySpirit 2015
 */
public class FindFriendsFragment extends BackStackFragment {

    private static final String TAG = FindFriendsFragment.class.getSimpleName();

    private AppCompatActivity activity;
    private MenuItem skipAction;
    private FriendsSuggestionsOptAdapter optionsAdapter;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;
    @InjectView(R.id.options_pagers)
    ViewPager optionsPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_friends, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        setHasOptionsMenu(true);

        optionsAdapter = new FriendsSuggestionsOptAdapter(activity.getSupportFragmentManager());
        optionsPager.setOffscreenPageLimit(0);
        optionsPager.setAdapter(optionsAdapter);

        tabLayout.setupWithViewPager(optionsPager);
        initTabs();
    }

    private void initTabs() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        try {
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_facebook);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public int getTitle() {
        return R.string.title_find_friends;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_in_left);
        } else {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        skipAction = menu.findItem(R.id.action_skip);
        skipAction.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_skip) {
            FlurryAgent.logEvent(TrackingEvents.UserSkippedFriendsSuggestions);
            activity.getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onTwitterButtonActivityResult(int requestCode, int resultCode, Intent data) {
        optionsAdapter.getItem(0).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.activity = (AppCompatActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        skipAction.setVisible(false);
    }
}
