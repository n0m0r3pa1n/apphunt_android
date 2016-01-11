package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiService;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadAppsApiEvent;
import com.apphunt.app.event_bus.events.api.apps.LoadSearchedAppsApiEvent;
import com.apphunt.app.event_bus.events.ui.ClearSearchEvent;
import com.apphunt.app.event_bus.events.ui.NetworkStatusChangeEvent;
import com.apphunt.app.event_bus.events.ui.SearchStatusEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.ui.adapters.DailyAppsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.listeners.EndlessRecyclerScrollListener;
import com.apphunt.app.ui.listview_items.AppHuntAdItem;
import com.apphunt.app.ui.listview_items.PaidAdItem;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.SoundsUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apptentive.android.sdk.Apptentive;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DailyAppsFragment extends BaseFragment {
    public static final String TAG = DailyAppsFragment.class.getSimpleName();

    private MainActivity activity;
    private DailyAppsAdapter dailyAppsAdapter;

    @InjectView(R.id.trending_list)
    RecyclerView rvTrendingApps;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.add_app)
    FloatingActionButton btnAddApp;

    @InjectView(R.id.reload)
    Button btnReload;
    private LinearLayoutManager layoutManager;

    private boolean shouldChangeDate;

    private OnEndReachedListener onEndReachedListener = new OnEndReachedListener() {
        @Override
        public void onEndReached() {
            FlurryWrapper.logEvent(TrackingEvents.UserScrolledDownAppList);
            loadApps();
        }
    };
    private int appsLoadedEventCount = 0;


    private void loadApps() {
        ApiService.getInstance(activity).loadApps(shouldChangeDate);
    }

    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;

    public DailyAppsFragment() {
        FlurryWrapper.logEvent(TrackingEvents.UserViewedTrendingApps);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_apps, container, false);
        ButterKnife.inject(this, view);
        initUi();
        ApiService.getInstance(activity).reloadApps();

        return view;
    }

    public void initUi() {
        ActionBarUtils.getInstance().showActionBarShadow();
        rvTrendingApps.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity());
        rvTrendingApps.setLayoutManager(layoutManager);
        rvTrendingApps.setHasFixedSize(true);

        dailyAppsAdapter = new DailyAppsAdapter(activity, rvTrendingApps);
        rvTrendingApps.setAdapter(dailyAppsAdapter);
        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(onEndReachedListener, layoutManager);
        rvTrendingApps.addOnScrollListener(endlessRecyclerScrollListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FlurryWrapper.logEvent(TrackingEvents.UserRefreshedTrendingApps);
                activity.getSupportActionBar().collapseActionView();
                reloadApps();
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void reloadApps() {
        appsLoadedEventCount = 0;
        dailyAppsAdapter.resetAdapter();
        endlessRecyclerScrollListener.reset();
        if(dailyAppsAdapter.getItemCount() == 0) {
            dailyAppsAdapter.addItem(0, new AppHuntAdItem());
        }
        ApiService.resetCalendars();
        ApiService.getInstance(activity).reloadApps();
    }

    @Override
    public int getTitle() {
        return R.string.title_home;
    }

    @OnClick(R.id.reload)
    public void onReloadApps() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        btnReload.setVisibility(View.GONE);
        reloadApps();
        btnAddApp.setVisibility(View.VISIBLE);
        rvTrendingApps.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.add_app)
    public void addApp() {
        startSelectAppFragment();
        SoundsUtils.performHapticFeedback(btnAddApp);
        Apptentive.engage(activity, "user.clicked.add.app");
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
    public void onAttach(Activity activity) {
        this.activity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);

    }

    @Subscribe
    public void onUserLogin(LoginEvent event) {
        reloadApps();
    }

    @Subscribe
    public void onAppsLoaded(LoadAppsApiEvent event) {
        if(event.getAppsList() == null) {
            shouldChangeDate = true;
        } else {
            shouldChangeDate = !event.getAppsList().haveMoreApps();
        }

        if(dailyAppsAdapter.getItemCount() == 0) {
            dailyAppsAdapter.addItem(0, new AppHuntAdItem());
        }

        if(appsLoadedEventCount % 2 == 0) {
            dailyAppsAdapter.addItem(new PaidAdItem());
        }

        dailyAppsAdapter.notifyAdapter(event.getAppsList());
        if(dailyAppsAdapter.getItemCount() < Constants.MIN_TOTAL_APPS_COUNT) {
            ApiService.getInstance(activity).loadApps(shouldChangeDate);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onAppsSearchLoaded(LoadSearchedAppsApiEvent event) {
        dailyAppsAdapter.showSearchResult(event.getAppsList().getApps());
    }

    @Subscribe
    public void onClearSearch(ClearSearchEvent event) {
        dailyAppsAdapter.clearSearch();
        rvTrendingApps.addOnScrollListener(endlessRecyclerScrollListener);
    }

    @Subscribe
    public void onSearchStatusChanged(SearchStatusEvent event) {
        if(event.isSearching()) {
            rvTrendingApps.removeOnScrollListener(endlessRecyclerScrollListener);
        } else {
            rvTrendingApps.addOnScrollListener(endlessRecyclerScrollListener);
        }
    }

    @Subscribe
    public void onNetworkStatus(NetworkStatusChangeEvent event) {
        if(event.isNetworkAvailable() && btnReload.getVisibility() == View.GONE) {
            btnReload.setVisibility(View.VISIBLE);
            rvTrendingApps.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            reloadApps();
        }
    }
}
