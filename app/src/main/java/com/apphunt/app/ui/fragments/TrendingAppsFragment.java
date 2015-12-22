package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.GetTrendingAppsApiEvent;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.listeners.EndlessRecyclerScrollListener;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-12-21.
 */
public class TrendingAppsFragment extends BaseFragment {

    public static final String TAG = TrendingAppsFragment.class.getSimpleName();
    private Activity activity;
    private TrendingAppsAdapter trendingAppsAdapter;

    private int page = 0, pageSize = 30;


    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.trending_list)
    RecyclerView rvTrendingApps;

    @InjectView(R.id.loading)
    CircularProgressBar loading;


    public static TrendingAppsFragment newInstance() {
        return new TrendingAppsFragment();
    }

    public TrendingAppsFragment() {

    }


    private OnEndReachedListener onEndReachedListener = new OnEndReachedListener() {
        @Override
        public void onEndReached() {
            FlurryWrapper.logEvent(TrackingEvents.UserScrolledDownTrendingAppList);
            loadTrendingApps();
        }
    };

    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending_apps, container, false);
        ButterKnife.inject(this, view);
        rvTrendingApps.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity());
        rvTrendingApps.setLayoutManager(layoutManager);
        rvTrendingApps.setHasFixedSize(true);


        trendingAppsAdapter = new TrendingAppsAdapter(activity, new ArrayList<BaseApp>());
        rvTrendingApps.setAdapter(trendingAppsAdapter);
        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(onEndReachedListener, layoutManager);
        endlessRecyclerScrollListener.setVisibleThreshold(15);
        rvTrendingApps.addOnScrollListener(endlessRecyclerScrollListener);


        loadTrendingApps();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading.setVisibility(View.VISIBLE);
                page = 0;
                rvTrendingApps.setAdapter(null);
                endlessRecyclerScrollListener.reset();
                trendingAppsAdapter.clearItems();
                FlurryWrapper.logEvent(TrackingEvents.UserRefreshedTrendingApps);
                swipeRefreshLayout.setRefreshing(false);
                loadTrendingApps();
                rvTrendingApps.setAdapter(trendingAppsAdapter);
            }
        });

        return view;
    }

    private void loadTrendingApps() {
        page++;
        ApiClient.getClient(activity).loadTrendingApps(LoginProviderFactory.get(activity).getUser().getId(), page, pageSize);
        Log.d(TAG, "onEndReached: page" + page);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onTrendingAppsReceived(GetTrendingAppsApiEvent event) {
        loading.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        Log.d(TAG, "onTrendingAppsReceived: " + event.getAppList().getPage());
        trendingAppsAdapter.addAll(event.getAppList().getApps());
    }
}
