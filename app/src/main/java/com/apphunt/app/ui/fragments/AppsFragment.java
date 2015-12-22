package com.apphunt.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.ui.adapters.AppsTabsPagerAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-12-21.
 */
public class AppsFragment extends BaseFragment {

    @InjectView(R.id.apps_tabs)
    ViewPager appsViewPager;

    @InjectView(R.id.tabs)
    TabLayout tabs;

    private AppsTabsPagerAdapter appsTabsPagerAdapter;

    public AppsFragment() {
        setFragmentTag(Constants.TAG_APPS_LIST_FRAGMENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        ButterKnife.inject(this, view);

        appsTabsPagerAdapter = new AppsTabsPagerAdapter(getChildFragmentManager());
        appsViewPager.setAdapter(appsTabsPagerAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabs.setupWithViewPager(appsViewPager);
    }

    @Override
    public int getTitle() {
        return R.string.title_home;
    }
}
