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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.AppsSearchResultEvent;
import com.apphunt.app.event_bus.events.api.collections.CollectionsSearchResultEvent;
import com.apphunt.app.ui.adapters.SearchResultsAdapter;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/8/15.
 * *
 * * NaughtySpirit 2015
 */
public class SearchFragment extends BaseFragment {
    public static final String TAG = SearchFragment.class.getSimpleName();

    private Activity activity;
    private View view;
    private String query;
    private String queryUrl;
    private LinearLayoutManager layoutManager;
    private SearchResultsAdapter resultsListAdapter;

    @InjectView(R.id.results_list)
    RecyclerView resultsList;

    @InjectView(R.id.loading)
    CircularProgressBar loader;

    @InjectView(R.id.no_results)
    RelativeLayout noResultsLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        resultsList.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity());
        resultsList.setLayoutManager(layoutManager);
        resultsList.setHasFixedSize(true);

        resultsListAdapter = new SearchResultsAdapter(activity, resultsList, noResultsLayout);
        resultsList.setAdapter(resultsListAdapter);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            loader.setVisibility(View.VISIBLE);
            return AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top_notification);
        } else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_top);
        }
    }

    @Override
    public int getTitle() {
        return R.string.title_search;
    }

    public void setQuery(String q) {
        String[] tags = q.split(" ");
        String query = "?";

        for (String tag : tags) {
            query += "names[]=" + tag + "&";
        }

        this.queryUrl = query;
        this.query = q;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(resultsListAdapter.getItemCount() > 0) {
            loader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_search).expandActionView();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQuery(query, false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Map<String, String> params = new HashMap<>();
                params.put("query", s);
                FlurryAgent.logEvent(TrackingEvents.UserSearchedForApp, params);

                setQuery(s);

                resultsListAdapter.resetAdapter();

                ApiClient.getClient(activity).getAppsByTags(queryUrl, 1, Constants.PAGE_SIZE, LoginProviderFactory.get(activity).getUser().getId());
                ApiClient.getClient(activity).getCollectionsByTags(queryUrl, 1, Constants.PAGE_SIZE, LoginProviderFactory.get(activity).getUser().getId());

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

    @Subscribe
    public void onAppsSearchResultsEvent(AppsSearchResultEvent event) {
        loader.progressiveStop();

        if (event.getApps().getTotalCount() > 0) {
            resultsListAdapter.onAppsSearchResultsEvent(event);
        } else {
            noResultsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onCollectionsSearchResultsEvent(CollectionsSearchResultEvent event) {
        loader.progressiveStop();

        if (event.getCollections().getTotalCount() > 0) {
            resultsListAdapter.onCollectionsSearchResultsEvent(event);
        } else {
            noResultsLayout.setVisibility(View.VISIBLE);
        }
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
}
