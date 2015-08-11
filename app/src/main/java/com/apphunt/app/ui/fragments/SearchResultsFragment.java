package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.AppsSearchResultEvent;
import com.apphunt.app.event_bus.events.api.collections.CollectionsSearchResultEvent;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-8-11.
 */
public class SearchResultsFragment extends BaseFragment {
    public static final String QUERY = "QUERY";
    public static final int COLLECTIONS_COUNT = 1;
    public static final int APPS_COUNT = 2;

    private Activity activity;
    private String searchTerm;
    private String query;

    @InjectView(R.id.loading)
    CircularProgressBar loader;

    @InjectView(R.id.apps)
    RecyclerView apps;

    @InjectView(R.id.collections)
    ListView collections;

    @InjectView(R.id.no_results)
    RelativeLayout noResultsLayout;

    @InjectView(R.id.collections_container)
    RelativeLayout collectionsContainer;

    @InjectView(R.id.apps_container)
    RelativeLayout appsContainer;

    private CollectionsAdapter collectionsAdapter;
    private TrendingAppsAdapter trendingAppsAdapter;

    public static SearchResultsFragment newInstance(String query) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QUERY, query);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getTitle() {
        return R.string.title_search;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        this.searchTerm = getArguments().getString(QUERY);
        updateQuery(searchTerm);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.inject(this, view);

        apps.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        apps.setLayoutManager(layoutManager);
        apps.setHasFixedSize(true);

        ApiClient.getClient(activity).getAppsByTags(query, 1, APPS_COUNT, LoginProviderFactory.get(activity).getUser().getId());
        ApiClient.getClient(activity).getCollectionsByTags(query, 1, COLLECTIONS_COUNT, LoginProviderFactory.get(activity).getUser().getId());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_search).expandActionView();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQuery(searchTerm, false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Map<String, String> params = new HashMap<>();
                params.put("query", s);
                FlurryAgent.logEvent(TrackingEvents.UserSearchedForApp, params);

                updateQuery(s);

                if (trendingAppsAdapter != null) trendingAppsAdapter.resetAdapter();
                if (collectionsAdapter != null) collectionsAdapter.resetAdapter();

                ApiClient.getClient(activity).getAppsByTags(query, 1, APPS_COUNT, LoginProviderFactory.get(activity).getUser().getId());
                ApiClient.getClient(activity).getCollectionsByTags(query, 1, COLLECTIONS_COUNT, LoginProviderFactory.get(activity).getUser().getId());

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    ((FragmentActivity) activity).getSupportFragmentManager().popBackStack();
                }
                return true;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Subscribe
    public void onAppsSearchResultsEvent(AppsSearchResultEvent event) {
        loader.progressiveStop();

        if (event.getApps().getTotalCount() > 0) {
            trendingAppsAdapter = new TrendingAppsAdapter(activity, apps);
            apps.setAdapter(trendingAppsAdapter);
            trendingAppsAdapter.showSearchResult(event.getApps().getApps());
            appsContainer.setVisibility(View.VISIBLE);
            hideNoResultsLayout();
        }

        if (event.getApps().getTotalCount() == 0 && (collectionsAdapter != null && collectionsAdapter.getCount() == 0)) {
            showNoResultsLayout();
        }
    }

    @Subscribe
    public void onCollectionsSearchResultsEvent(CollectionsSearchResultEvent event) {
        loader.progressiveStop();

        if (event.getCollections().getTotalCount() > 0) {
            collectionsAdapter = new CollectionsAdapter(getActivity() ,event.getCollections().getCollections());
            collections.setAdapter(collectionsAdapter);
            collectionsContainer.setVisibility(View.VISIBLE);
            hideNoResultsLayout();
        }

        if (event.getCollections().getTotalCount() == 0 && (trendingAppsAdapter != null && trendingAppsAdapter.getItemCount() == 0)) {
            showNoResultsLayout();
        }
    }

    private void hideNoResultsLayout() {
        noResultsLayout.setVisibility(View.GONE);
    }

    private void showNoResultsLayout() {
        collectionsContainer.setVisibility(View.GONE);
        appsContainer.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.VISIBLE);
    }

    private void updateQuery(String q) {
        String[] tags = q.split(" ");
        String query = "?";

        for (String tag : tags) {
            query += "names[]=" + tag + "&";
        }

        this.query = query;
    }
}