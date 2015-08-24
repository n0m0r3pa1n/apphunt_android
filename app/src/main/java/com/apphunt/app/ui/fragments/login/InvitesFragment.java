package com.apphunt.app.ui.fragments.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.apphunt.app.R;
import com.apphunt.app.ui.adapters.login.InviteOptionsAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/24/15.
 * *
 * * NaughtySpirit 2015
 */
public class InvitesFragment extends BackStackFragment {

    private AppCompatActivity activity;
    private InviteOptionsAdapter optionsAdapter;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;

    @InjectView(R.id.options_pagers)
    ViewPager optionsPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        optionsAdapter = new InviteOptionsAdapter(activity.getSupportFragmentManager(), activity);

        optionsPager.setOffscreenPageLimit(1);
        optionsPager.setAdapter(optionsAdapter);

        tabLayout.setupWithViewPager(optionsPager);
    }

    @OnClick(R.id.skip)
    public void onSkipClick() {
        activity.getSupportFragmentManager().popBackStack();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }
}
