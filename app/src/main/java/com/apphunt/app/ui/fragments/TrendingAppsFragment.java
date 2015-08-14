package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiService;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadAppsApiEvent;
import com.apphunt.app.event_bus.events.api.apps.LoadSearchedAppsApiEvent;
import com.apphunt.app.event_bus.events.ui.ClearSearchEvent;
import com.apphunt.app.event_bus.events.ui.NetworkStatusChangeEvent;
import com.apphunt.app.event_bus.events.ui.SearchStatusEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.SoundsUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TrendingAppsFragment extends BaseFragment {
    public static final String TAG = TrendingAppsFragment.class.getSimpleName();

    private boolean isEndOfList = false;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private MainActivity activity;
    private TrendingAppsAdapter trendingAppsAdapter;

    @InjectView(R.id.trending_list)
    RecyclerView rvTrendingApps;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.add_app)
    FloatingActionButton btnAddApp;

    @InjectView(R.id.reload)
    Button btnReload;
    private LinearLayoutManager layoutManager;

    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (ConnectivityUtils.isNetworkAvailable(getActivity())) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    if (isEndOfList) {
                        LoadersUtils.showBottomLoader(activity,
                                SharedPreferencesHelper.getBooleanPreference(Constants.IS_SOUNDS_ENABLED));
                        FlurryAgent.logEvent(TrackingEvents.UserScrolledDownAppList);
                        ApiService.getInstance(activity).loadAppsForPreviousDate();
                    }
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = layoutManager.getChildCount();
            totalItemCount = layoutManager.getItemCount();
            pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

            isEndOfList = (pastVisiblesItems + visibleItemCount) == totalItemCount;
        }
    };

    public TrendingAppsFragment() {
        setFragmentTag(Constants.TAG_APPS_LIST_FRAGMENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending_apps, container, false);
        ButterKnife.inject(this, view);
        initUi();
        ApiService.getInstance(activity).loadAppsForToday();

        return view;
    }

    public void initUi() {
        ActionBarUtils.getInstance().showActionBarShadow();
        rvTrendingApps.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity());
        rvTrendingApps.setLayoutManager(layoutManager);
        rvTrendingApps.setHasFixedSize(true);

        trendingAppsAdapter = new TrendingAppsAdapter(activity, rvTrendingApps);
        rvTrendingApps.setAdapter(trendingAppsAdapter);
        rvTrendingApps.addOnScrollListener(onScrollListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activity.getSupportActionBar().collapseActionView();
                trendingAppsAdapter.resetAdapter();
                ApiService.getInstance(activity).loadAppsForToday();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public int getTitle() {
        return R.string.title_home;
    }

    @OnClick(R.id.reload)
    public void reloadApps() {
        btnReload.setVisibility(View.GONE);
        trendingAppsAdapter.resetAdapter();
        ApiService.getInstance(activity).loadAppsForToday();
        btnAddApp.setVisibility(View.VISIBLE);
        rvTrendingApps.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.add_app)
    public void addApp() {
        startSelectAppFragment();
        SoundsUtils.performHapticFeedback(btnAddApp);
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
        trendingAppsAdapter.resetAdapter();
        ApiService.getInstance(activity).loadAppsForToday();
    }

    @Subscribe
    public void onUserLogout(LogoutEvent event) {
        trendingAppsAdapter.resetAdapter();
        ApiService.getInstance(activity).loadAppsForToday();
    }

    @Subscribe
    public void onAppsLoaded(LoadAppsApiEvent event) {
        LoadersUtils.hideBottomLoader(activity);
        trendingAppsAdapter.notifyAdapter(event.getAppsList());
    }

    @Subscribe
    public void onAppsSearchLoaded(LoadSearchedAppsApiEvent event) {
        trendingAppsAdapter.showSearchResult(event.getAppsList().getApps());
    }

    @Subscribe
    public void onClearSearch(ClearSearchEvent event) {
        trendingAppsAdapter.clearSearch();
        rvTrendingApps.addOnScrollListener(onScrollListener);
    }

    @Subscribe
    public void onSearchStatusChanged(SearchStatusEvent event) {
        if(event.isSearching()) {
            rvTrendingApps.removeOnScrollListener(onScrollListener);
        } else {
            rvTrendingApps.addOnScrollListener(onScrollListener);
        }
    }

    @Subscribe
    public void onNetworkStatus(NetworkStatusChangeEvent event) {
        if(event.isNetworkAvailable() && btnReload.getVisibility() == View.GONE) {
            btnReload.setVisibility(View.VISIBLE);
            rvTrendingApps.setVisibility(View.GONE);
        } else {
            reloadApps();
        }
    }
}
