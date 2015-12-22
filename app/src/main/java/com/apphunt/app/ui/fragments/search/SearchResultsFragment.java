package com.apphunt.app.ui.fragments.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.apps.AppsSearchResultEvent;
import com.apphunt.app.event_bus.events.api.collections.CollectionsSearchResultEvent;
import com.apphunt.app.ui.adapters.DailyAppsAdapter;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.layout.CustomLayoutManager;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-8-11.
 */
public class SearchResultsFragment extends BackStackFragment {
    public static final String TAG = SearchResultsFragment.class.getSimpleName();
    public static final String QUERY = "QUERY";
    public static final int COLLECTIONS_COUNT = 1;
    public static final int APPS_COUNT = 2;

    private int appsCount = -1;
    private int collectionsCount = -1;

    private AppCompatActivity activity;
    private String query;

    @InjectView(R.id.apps_loader)
    CircularProgressBar appsLoader;

    @InjectView(R.id.collections_loader)
    CircularProgressBar collectionsLoader;

    @InjectView(R.id.apps)
    RecyclerView apps;

    @InjectView(R.id.collections)
    ListView collections;

    @InjectView(R.id.apps_container)
    RelativeLayout appsContainer;

    @InjectView(R.id.collections_container)
    RelativeLayout collectionsContainer;

    @InjectView(R.id.no_results)
    RelativeLayout noResultsLayout;

    @InjectView(R.id.more_apps)
    TextView moreApps;

    @InjectView(R.id.more_collections)
    TextView moreCollections;

    @InjectView(R.id.apps_header)
    TextView appsHeader;

    @InjectView(R.id.collections_header)
    TextView collectionsHeader;

    @InjectView(R.id.vs_no_collections)
    ViewStub noCollectionsView;

    @InjectView(R.id.vs_no_apps)
    ViewStub noAppsView;

    private CollectionsAdapter collectionsAdapter;
    private DailyAppsAdapter dailyAppsAdapter;

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
        query = getArguments().getString(QUERY);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.inject(this, view);

        apps.setItemAnimator(new DefaultItemAnimator());
        CustomLayoutManager layoutManager = new CustomLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        apps.setLayoutManager(layoutManager);
        apps.setHasFixedSize(true);

        showLoaders();
        ApiClient.getClient(activity).getAppsByTags(query, 1, APPS_COUNT, LoginProviderFactory.get(activity).getUser().getId());
        ApiClient.getClient(activity).getCollectionsByTags(query, 1, COLLECTIONS_COUNT, LoginProviderFactory.get(activity).getUser().getId());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isSearchShowing()) {
            return;
        }
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setVisible(true);
        searchMenuItem.expandActionView();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQuery(query, false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Map<String, String> params = new HashMap<>();
                params.put("query", s);
                FlurryWrapper.logEvent(TrackingEvents.UserSearchedForApp, params);

                if (dailyAppsAdapter != null) dailyAppsAdapter.resetAdapter();
                if (collectionsAdapter != null) collectionsAdapter.resetAdapter();

                query = s;
                showLoaders();
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
    }

    private void showLoaders() {
        collectionsLoader.setVisibility(View.VISIBLE);
        appsLoader.setVisibility(View.VISIBLE);
    }

    private boolean isSearchShowing() {
        return activity.getSupportFragmentManager().getBackStackEntryCount() > 1;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @OnClick(R.id.more_apps)
    public void moreApps() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedMoreApps);
        displayMoreApps();
    }

    private void displayMoreApps() {
                activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, SearchAppsFragment.newInstance(query), Constants.TAG_SEARCH_APPS_FRAGMENT)
                .addToBackStack(Constants.TAG_SEARCH_APPS_FRAGMENT)
                .commit();
    }

    @OnClick(R.id.more_collections)
    public void moreCollections() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedMoreCollections);
        displayMoreCollections();
    }

    private void displayMoreCollections() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, SearchCollectionsFragment.newInstance(query), Constants.TAG_SEARCH_COLLECTIONS_FRAGMENT)
                .addToBackStack(Constants.TAG_SEARCH_COLLECTIONS_FRAGMENT)
                .commit();
    }

    @Subscribe
    public void onAppsSearchResultsEvent(AppsSearchResultEvent event) {
        if(isSearchShowing()) {
            return;
        }

        appsLoader.setVisibility(View.GONE);

        int totalCount = event.getApps().getTotalCount();
        if(totalCount <= 0) {
            noAppsView.setVisibility(View.VISIBLE);
        } else {
            noAppsView.setVisibility(View.GONE);
            dailyAppsAdapter = new DailyAppsAdapter(activity, apps);
            apps.setAdapter(dailyAppsAdapter);
            dailyAppsAdapter.showSearchResult(event.getApps().getApps());
        }
        appsHeader.setText(getResources().getQuantityString(R.plurals.apps, totalCount, totalCount));
        setSearchResults(totalCount, collectionsCount);
    }

    @Subscribe
    public void onCollectionsSearchResultsEvent(CollectionsSearchResultEvent event) {
        if(isSearchShowing()) {
            return;
        }

        collectionsLoader.setVisibility(View.GONE);

        int totalCount = event.getCollections().getTotalCount();
        if(totalCount <= 0) {
            noCollectionsView.setVisibility(View.VISIBLE);
        } else {
            noCollectionsView.setVisibility(View.GONE);
            collectionsAdapter = new CollectionsAdapter(activity ,event.getCollections().getCollections());
            collections.setAdapter(collectionsAdapter);
        }

        collectionsHeader.setText(getResources().getQuantityString(R.plurals.collections, totalCount, totalCount));
        setSearchResults(appsCount, totalCount);
    }

    public void setSearchResults(int apps, int collections) {
        this.appsCount = apps;
        this.collectionsCount = collections;
        if(isResultFromQueriesEmpty()) {
            noResultsLayout.setVisibility(View.VISIBLE);
            collectionsContainer.setVisibility(View.INVISIBLE);
            appsContainer.setVisibility(View.INVISIBLE);
            return;
        } else {
            collectionsContainer.setVisibility(View.VISIBLE);
            appsContainer.setVisibility(View.VISIBLE);
            noResultsLayout.setVisibility(View.GONE);
        }

        setupMoreButtons();
    }

    private void setupMoreButtons() {
        if(collectionsCount <= 1) {
            moreCollections.setVisibility(View.INVISIBLE);
        } else {
            moreCollections.setVisibility(View.VISIBLE);
        }

        if(appsCount <= 2) {
            moreApps.setVisibility(View.INVISIBLE);
        } else {
            moreApps.setVisibility(View.VISIBLE);
        }
    }

    private boolean isResultFromQueriesEmpty() {
        return appsCount == 0 && collectionsCount == 0;
    }

}
