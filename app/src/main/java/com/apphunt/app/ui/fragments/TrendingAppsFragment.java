package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.apphunt.app.event_bus.events.api.apps.LoadAppsApiEvent;
import com.apphunt.app.event_bus.events.api.apps.LoadSearchedAppsApiEvent;
import com.apphunt.app.event_bus.events.ui.ClearSearchEvent;
import com.apphunt.app.event_bus.events.ui.NetworkStatusChangeEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.flurry.android.FlurryAgent;
import com.quentindommerc.superlistview.SuperListview;
import com.shamanland.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TrendingAppsFragment extends BaseFragment implements AbsListView.OnScrollListener {
    public static final String TAG = TrendingAppsFragment.class.getSimpleName();

    private boolean isEndOfList = false;
    private MainActivity activity;
    private TrendingAppsAdapter trendingAppsAdapter;

    @InjectView(R.id.trending_list)
    SuperListview lvTrendingApps;

    @InjectView(R.id.add_app)
    FloatingActionButton btnAddApp;

    @InjectView(R.id.reload)
    Button btnReload;

    public TrendingAppsFragment() {
        setTitle(R.string.title_home);
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
        ActionBarUtils.getInstance().setTitle(R.string.title_home);

        trendingAppsAdapter = new TrendingAppsAdapter(activity, lvTrendingApps);
        lvTrendingApps.setAdapter(trendingAppsAdapter);
        lvTrendingApps.setOnScrollListener(this);
        lvTrendingApps.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activity.getSupportActionBar().collapseActionView();
                trendingAppsAdapter.resetAdapter();
                ApiService.getInstance(activity).loadAppsForToday();
            }
        });

    }

    @OnClick(R.id.reload)
    public void reloadApps() {
        btnReload.setVisibility(View.GONE);
        trendingAppsAdapter.resetAdapter();
        ApiService.getInstance(activity).loadAppsForToday();
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
    public void onAttach(Activity activity) {
        this.activity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isEndOfList = (firstVisibleItem + visibleItemCount) == totalItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (ConnectivityUtils.isNetworkAvailable(getActivity())) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
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
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
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
    }

    @Subscribe
    public void onNetworkStatus(NetworkStatusChangeEvent event) {
        if(event.isNetworkAvailable() && btnReload.getVisibility() == View.GONE) {
            btnReload.setVisibility(View.VISIBLE);
            lvTrendingApps.setVisibility(View.GONE);
        }
    }
}
