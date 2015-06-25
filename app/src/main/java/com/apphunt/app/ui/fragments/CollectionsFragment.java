package com.apphunt.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.apphunt.app.R;
import com.apphunt.app.ui.adapters.CollectionsPagerAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-25.
 */
public class CollectionsFragment extends BaseFragment {
    @InjectView(R.id.collection_tabs)
    ViewPager pager;

    @InjectView(R.id.tabLayout)
    TabLayout tabLayout;

    PagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections, container, false);
        ButterKnife.inject(this, view);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_collecton_select).setTag(0));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_collecton_not_select).setTag(1));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_collecton_not_select).setTag(2));
        pagerAdapter = new CollectionsPagerAdapter(getActivity().getSupportFragmentManager());
        pagerAdapter = new CollectionsPagerAdapter(getActivity().getSupportFragmentManager());
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        pager.setAdapter(pagerAdapter);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Object tag = tab.getTag();
                pager.setCurrentItem((Integer) tag);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
}
