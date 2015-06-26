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

    private TabLayout.Tab previousSelectedTab;

    PagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections, container, false);
        ButterKnife.inject(this, view);
        initTabs();

        pagerAdapter = new CollectionsPagerAdapter(getActivity().getSupportFragmentManager());
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        pager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tag = (Integer)tab.getTag();
                setTabSelectedIcon(tab);
                pager.setCurrentItem((Integer) tag);
                resetTabIcon(previousSelectedTab);
                previousSelectedTab = tab;
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



    private void initTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_collecton_not_select).setTag(0));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_favorite_not_select).setTag(1));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_mycollection_not_select).setTag(2));
        previousSelectedTab = tabLayout.getTabAt(0);
        setTabSelectedIcon(previousSelectedTab);
    }

    private void resetTabIcon(TabLayout.Tab tab) {
        if(tab == null)
            return;
        int tag = (int) tab.getTag();
        switch(tag) {
            case 0:
                tab.setIcon(R.drawable.ic_collecton_not_select);
                break;
            case 1:
                tab.setIcon(R.drawable.ic_favorite_not_select);
                break;
            case 2:
                tab.setIcon(R.drawable.ic_mycollection_not_select);
                break;
        }
    }

    private void setTabSelectedIcon(TabLayout.Tab tab) {
        int tag = (int) tab.getTag();
        switch(tag) {
            case 0:
                tab.setIcon(R.drawable.ic_collecton_select);
                break;
            case 1:
                tab.setIcon(R.drawable.ic_favorite_select);
                break;
            case 2:
                tab.setIcon(R.drawable.ic_mycollection_select);
                break;
        }
    }
}
