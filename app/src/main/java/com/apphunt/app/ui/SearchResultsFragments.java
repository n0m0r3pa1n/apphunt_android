package com.apphunt.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.AppsSearchResultEvent;
import com.apphunt.app.event_bus.events.api.collections.CollectionsSearchResultEvent;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-8-11.
 */
public class SearchResultsFragments extends BaseFragment {
    public static final String QUERY = "QUERY";
    public static final int COLLECTIONS_COUNT = 1;
    public static final int APPS_COUNT = 2;

    private Activity activity;
    private String query;

    @InjectView(R.id.loading)
    CircularProgressBar loader;

    @InjectView(R.id.apps)
    RecyclerView apps;

    @InjectView(R.id.collections)
    ListView collections;

    private CollectionsAdapter collectionsAdapter;
    private TrendingAppsAdapter trendingAppsAdapter;

    public static SearchResultsFragments newInstance(String query) {
        SearchResultsFragments fragment = new SearchResultsFragments();
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
        updateQuery(getArguments().getString(QUERY));
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
        }
    }

    @Subscribe
    public void onCollectionsSearchResultsEvent(CollectionsSearchResultEvent event) {
        loader.progressiveStop();

        if (event.getCollections().getTotalCount() > 0) {
            collectionsAdapter = new CollectionsAdapter(getActivity() ,event.getCollections().getCollections());
            collections.setAdapter(collectionsAdapter);
        }
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
