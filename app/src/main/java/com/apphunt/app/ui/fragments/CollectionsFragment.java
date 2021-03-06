package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.adapters.collections.CollectionsPagerAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SoundsUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CollectionsFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    public static final String TAG = CollectionsFragment.class.getSimpleName();

    @InjectView(R.id.collection_tabs)
    ViewPager pager;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;

    private TabLayout.Tab previousSelectedTab;

    private CollectionsPagerAdapter pagerAdapter;
    private int title;
    private  String creatorId;
    private Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections, container, false);
        ButterKnife.inject(this, view);
        creatorId = LoginProviderFactory.get(activity).isUserLoggedIn() ? LoginProviderFactory.get(activity).getUser().getId() : null;
        pagerAdapter = new CollectionsPagerAdapter(getChildFragmentManager(), creatorId);
        initTabs();

        pager.setOffscreenPageLimit(1);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(this);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tag = (Integer) tab.getTag();
                setTabSelectedIcon(tab);
                pager.setCurrentItem(tag);
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

        ActionBarUtils.getInstance().hideActionBarShadow();
        return view;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
        ActionBarUtils.getInstance().showActionBarShadow();
    }

    @Subscribe
    public void onUserLogin(LoginEvent event) {
        creatorId = LoginProviderFactory.get(activity).getUser().getId();
        pagerAdapter = null;
        pager.setAdapter(null);
        pagerAdapter = new CollectionsPagerAdapter(getChildFragmentManager(), creatorId);
        pager.setAdapter(pagerAdapter);
    }

    @Subscribe
    public void onUserLogout(LogoutEvent event) {
        creatorId = null;
        pagerAdapter = null;
        pager.setAdapter(null);
        pagerAdapter = new CollectionsPagerAdapter(getChildFragmentManager(), creatorId);
        pager.setAdapter(pagerAdapter);
    }

    private void initTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_collecton_not_select).setTag(0));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_favorite_not_select).setTag(1));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_mycollection_not_select_one).setTag(2));
        previousSelectedTab = tabLayout.getTabAt(0);
        setTabSelectedIcon(previousSelectedTab);
        updateActionBarTitle(0);
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
                tab.setIcon(R.drawable.ic_mycollection_not_select_one);
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
                tab.setIcon(R.drawable.ic_mycollection_select_one);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updateActionBarTitle(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick(R.id.add_collection)
    public void openCreateCollectionFragment(View view) {
        if(LoginProviderFactory.get(activity).isUserLoggedIn()) {
            NavUtils.getInstance((AppCompatActivity) activity).presentCreateCollectionFragment();
        } else {
            LoginUtils.showLoginFragment(false, R.string.login_info_create_collection);
        }

        SoundsUtils.performHapticFeedback(view);
    }

    private void updateActionBarTitle(int position) {
        title = ((BaseFragment)pagerAdapter.getItem(position)).getTitle();
        ActionBarUtils.getInstance().setTitle(title);
    }

    @Override
    public int getTitle() {
        return title;
    }
}
