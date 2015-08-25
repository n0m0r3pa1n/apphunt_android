package com.apphunt.app.ui.fragments.profile.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserAppsApiEvent;
import com.apphunt.app.ui.adapters.SearchAppsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class AppsFragment extends BaseFragment {
    public static final String CREATOR_ID = "CREATOR_ID";
    @InjectView(R.id.items)
    ScrollRecyclerView items;

    @InjectView(R.id.loading)
    CircularProgressBar loader;

    @InjectView(R.id.vs_no_apps)
    ViewStub vsNoApps;

    int currentPage = 0;
    private AppCompatActivity activity;
    private SearchAppsAdapter adapter;
    private String userId;
    private String creatorId;

    public static AppsFragment newInstance(String creatorId) {
        Bundle bundle = new Bundle();
        bundle.putString(CREATOR_ID, creatorId);
        AppsFragment fragment = new AppsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_items, container, false);
        ButterKnife.inject(this, view);
        creatorId = getArguments().getString(CREATOR_ID);
        if(LoginProviderFactory.get(activity).isUserLoggedIn()) {
            userId = LoginProviderFactory.get(activity).getUser().getId();
        }

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onUserApps(GetUserAppsApiEvent event) {
        loader.setVisibility(View.GONE);
        items.hideBottomLoader();
        if(event.getApps() == null || event.getApps().getTotalCount() == 0) {
            vsNoApps.setVisibility(View.VISIBLE);
            return;
        }

        if(adapter == null) {
            adapter = new SearchAppsAdapter(getActivity(), event.getApps().getApps());
            items.setAdapter(adapter, event.getApps().getTotalCount());
        } else {
            adapter.addApps(event.getApps().getApps());
        }
    }

    private void getApps() {
        currentPage++;
        ApiClient.getClient(activity).getUserApps(creatorId, userId, new Pagination(currentPage, 5));
    }
}
