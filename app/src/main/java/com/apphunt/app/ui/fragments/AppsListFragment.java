package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiService;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.LoadAppsEvent;
import com.apphunt.app.event_bus.events.auth.LoginEvent;
import com.apphunt.app.event_bus.events.auth.LogoutEvent;
import com.apphunt.app.event_bus.events.votes.AppVoteEvent;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.ui.interfaces.OnNetworkStateChange;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.TrackingEvents;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.flurry.android.FlurryAgent;
import com.quentindommerc.superlistview.SuperListview;
import com.shamanland.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class AppsListFragment extends BaseFragment implements AbsListView.OnScrollListener, OnNetworkStateChange {

    private boolean endOfList = false;
    private MainActivity activity;
    private TrendingAppsAdapter trendingAppsAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(Constants.TAG_NOTIFICATION_FRAGMENT);

            if (!ConnectivityUtils.isNetworkAvailable(context)) {
                if (fragment == null) {
                    NotificationsUtils.showNotificationFragment(((ActionBarActivity) context), getString(R.string.notification_no_internet), true, false);
                }

                btnAddApp.setVisibility(View.INVISIBLE);
                lvTrendingApps.setVisibility(View.GONE);
                trendingAppsAdapter.clearAdapter();
                LoadersUtils.showCenterLoader(activity);
            } else {
                if (fragment != null) {
                    activity.getSupportFragmentManager().popBackStack(Constants.TAG_NOTIFICATION_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                onNetworkAvailable();
            }
        }
    };

    @InjectView(R.id.trending_list)
    SuperListview lvTrendingApps;

    @InjectView(R.id.add_app)
    FloatingActionButton btnAddApp;

    @InjectView(R.id.reload)
    Button btnReload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_list, container, false);
        ButterKnife.inject(this, view);
        initUi();
        ApiService.loadAppsForToday(activity);

        return view;
    }

    public void initUi() {
        trendingAppsAdapter = new TrendingAppsAdapter(activity, lvTrendingApps);
        lvTrendingApps.setAdapter(trendingAppsAdapter);
        lvTrendingApps.setOnScrollListener(this);
        lvTrendingApps.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activity.getSupportActionBar().collapseActionView();
                trendingAppsAdapter.resetAdapter();
            }
        });

    }

    @OnClick(R.id.reload)
    public void reloadApps() {
        btnReload.setVisibility(View.GONE);
        trendingAppsAdapter.resetAdapter();
        btnAddApp.setVisibility(View.VISIBLE);
        lvTrendingApps.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.add_app)
    public void addApp() {
        startSelectAppFragment();
        btnAddApp.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    private void startSelectAppFragment() {
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, new SelectAppFragment(), Constants.TAG_SELECT_APP_FRAGMENT)
                .addToBackStack(Constants.TAG_SELECT_APP_FRAGMENT)
                .commit();

        activity.getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onNetworkAvailable() {
        if (trendingAppsAdapter.getCount() == 0) {
            LoadersUtils.hideCenterLoader(activity);
            btnReload.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        this.activity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public int getTitle() {
        return super.getTitle();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        endOfList = (firstVisibleItem + visibleItemCount) == totalItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (ConnectivityUtils.isNetworkAvailable(getActivity())) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (endOfList && trendingAppsAdapter.couldLoadMoreApps()) {
                    FlurryAgent.logEvent(TrackingEvents.UserScrolledDownAppList);
                    ApiService.loadAppsForPreviousDate(activity);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        activity.registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        activity.unregisterReceiver(networkChangeReceiver);
    }

    @Subscribe
    public void onUserLogin(LoginEvent event) {
        trendingAppsAdapter.resetAdapter();
    }

    @Subscribe
    public void onUserLogout(LogoutEvent event) {
        trendingAppsAdapter.resetAdapter();
    }

    @Subscribe
    public void onAppVote(AppVoteEvent event) {
        trendingAppsAdapter.resetAdapter(event.getPosition());
    }

    @Subscribe
    public void onAppsLoaded(LoadAppsEvent event) {
        LoadersUtils.hideBottomLoader(activity);
        trendingAppsAdapter.notifyAdapter(event.getAppsList());
    }

    private void getApps() {
        LoadersUtils.showCenterLoader(activity);
        ApiService.loadAppsForToday(activity);
    }
}
