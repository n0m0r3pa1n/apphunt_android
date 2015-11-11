package com.apphunt.app.ui.fragments.invites;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import com.apphunt.app.ui.adapters.invite.InviteOptionsAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.ui.NavUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/24/15.
 * *
 * * NaughtySpirit 2015
 */
public class InvitesFragment extends BackStackFragment {

    private AppCompatActivity activity;
    private InviteOptionsAdapter optionsAdapter;
    private MenuItem skipAction;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;
    @InjectView(R.id.options_pagers)
    ViewPager optionsPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FlurryWrapper.logEvent(TrackingEvents.UserViewedInvites);
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        setHasOptionsMenu(true);

        optionsAdapter = new InviteOptionsAdapter(activity.getSupportFragmentManager(), activity);
        optionsPager.setOffscreenPageLimit(1);
        optionsPager.setAdapter(optionsAdapter);

        tabLayout.setupWithViewPager(optionsPager);
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
            FlurryWrapper.logEvent(TrackingEvents.UserSkippedInvitation);

            activity.getSupportFragmentManager().popBackStack();
            NavUtils.getInstance(activity).presentFindFriendsFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getTitle() {
        return R.string.invite;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        skipAction.setVisible(false);
    }
}
