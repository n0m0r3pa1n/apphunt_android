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
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.AppsList;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.GetFavouriteAppsApiEvent;
import com.apphunt.app.ui.adapters.SearchAppsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FavouriteAppsFragment extends BaseFragment {
    public static final String FAVOURITED_BY = "FAVOURITED_BY";
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
    private String favouritedBy;

    public static FavouriteAppsFragment newInstance(String favouritedBy) {
        Bundle bundle = new Bundle();
        bundle.putString(FAVOURITED_BY, favouritedBy);
        FavouriteAppsFragment fragment = new FavouriteAppsFragment();
        fragment.setArguments(bundle);

        FlurryAgent.logEvent(TrackingEvents.UserViewedFavouriteApp);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_items, container, false);
        ButterKnife.inject(this, view);
        favouritedBy = getArguments().getString(FAVOURITED_BY);
        if(LoginProviderFactory.get(activity).isUserLoggedIn()) {
            userId = LoginProviderFactory.get(activity).getUser().getId();
        }

        getFavouriteApps();
        items.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getFavouriteApps();
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
    public void onUserApps(GetFavouriteAppsApiEvent event) {
        loader.setVisibility(View.GONE);
        AppsList appsList = event.getAppsList();
        if(appsList == null || appsList.getTotalCount() == 0) {
            vsNoApps.setVisibility(View.VISIBLE);
            return;
        }

        ArrayList<App> apps = appsList.getApps();
        if(adapter == null) {
            adapter = new SearchAppsAdapter(getActivity(), apps, userId);
            items.setAdapter(adapter, appsList.getTotalCount());
        } else {
            adapter.addApps(apps);
        }
    }

    private void getFavouriteApps() {
        currentPage++;
        ApiClient.getClient(activity).getFavouriteApps(favouritedBy, userId, new Pagination(currentPage, Constants.PAGE_SIZE));
    }
}
