package com.apphunt.app.ui.fragments.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.events.api.apps.AppsSearchResultEvent;
import com.apphunt.app.ui.adapters.SearchAppsAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class SearchAppsFragment extends BackStackFragment {

    public static final String QUERY = "QUERY";


    @InjectView(R.id.items)
    ScrollRecyclerView items;

    @InjectView(R.id.loading)
    CircularProgressBar loader;

    int currentPage = 0;
    String query = "";

    private AppCompatActivity activity;
    private SearchAppsAdapter adapter;

    public static SearchAppsFragment newInstance(String query) {

        Bundle args = new Bundle();
        args.putString(QUERY, query);
        SearchAppsFragment fragment = new SearchAppsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_items, container, false);
        ButterKnife.inject(this, view);

        query = getArguments().getString(QUERY);
        ActionBarUtils.getInstance().setTitle(query);
        getApps();
        items.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getApps();
            }
        });
        return view;
    }

    @Override
    public String getStringTitle() {
        return query;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Subscribe
    public void onAppsSearchResultsEvent(AppsSearchResultEvent event) {
        loader.setVisibility(View.GONE);

        if(adapter == null) {
            adapter = new SearchAppsAdapter(getActivity(), event.getApps().getApps());
            items.setAdapter(adapter, event.getApps().getTotalCount());
        } else {
            adapter.addApps(event.getApps().getApps());
        }
    }

    private void getApps() {
        currentPage++;
        ApiClient.getClient(activity).getAppsByTags(query, currentPage, Constants.PAGE_SIZE, LoginProviderFactory.get(activity).getUser().getId());
    }
}
