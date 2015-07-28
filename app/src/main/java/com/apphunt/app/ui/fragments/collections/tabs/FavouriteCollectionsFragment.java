package com.apphunt.app.ui.fragments.collections.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.DeleteCollectionEvent;
import com.apphunt.app.event_bus.events.api.collections.FavouriteCollectionEvent;
import com.apphunt.app.event_bus.events.api.collections.GetFavouriteCollectionsEvent;
import com.apphunt.app.event_bus.events.api.collections.UnfavouriteCollectionEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollListView;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-26.
 */
public class FavouriteCollectionsFragment extends BaseFragment {
    public static final String TAG = FavouriteCollectionsFragment.class.getSimpleName();
    private CollectionsAdapter adapter;
    private int currentPage = 0;

    @InjectView(R.id.all_collections)
    ScrollListView allCollections;

    @InjectView(R.id.vs_no_collection)
    ViewStub vsNoCollection;

    View view;

    @Override
    public int getTitle() {
        return R.string.title_favourite_collection;
    }

    public static FavouriteCollectionsFragment newInstance() {
        return new FavouriteCollectionsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FlurryAgent.logEvent(TrackingEvents.UserViewedFavouriteCollections);
        view  = inflater.inflate(R.layout.fragment_all_collections, container, false);
        ButterKnife.inject(this, view);

        getFavouriteCollections();

        allCollections.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getFavouriteCollections();
            }
        });
        return view;
    }

    private void getFavouriteCollections() {
        if (LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            currentPage++;
            ApiClient.getClient(getActivity()).getFavouriteCollections(
                    LoginProviderFactory.get(getActivity()).getUser().getId(), currentPage, Constants.PAGE_SIZE);
            hideEmptyView();
        } else {
            showEmptyView();
        }
    }

    private void hideEmptyView() {
        vsNoCollection.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        vsNoCollection.setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.text)).setText(getResources().getString(R.string.no_favourite_collections));
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onCollectionFavourited(FavouriteCollectionEvent event) {
        if (adapter != null) {
            adapter.addCollection(event.getCollection());
            if(adapter.getCount() > 0) {
                hideEmptyView();
            }
        }
    }

    @Subscribe
    public void onCollectionUnfavourited(UnfavouriteCollectionEvent event) {
        if (adapter != null) {
            adapter.removeCollection(event.getCollectionId());
            if(adapter.getCount() == 0) {
                showEmptyView();
            }
        }
    }

    @Subscribe
    public void onFavouriteCollectionReceived(GetFavouriteCollectionsEvent event) {
        allCollections.hideBottomLoader();

        if (adapter == null) {
            adapter = new CollectionsAdapter(getActivity(), event.getAppsCollection().getCollections());
            allCollections.setAdapter(adapter, event.getAppsCollection().getTotalCount());
        } else {
            int currentSize = adapter.getCount();
            adapter.addAllCollections(event.getAppsCollection().getCollections());
            allCollections.smoothScrollToPosition(currentSize);
        }

        if (event.getAppsCollection().getCollections().size() == 0) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    @Subscribe
    public void onCollectionDeleted(DeleteCollectionEvent event) {
        String collectionId = event.getCollectionId();
        adapter.removeCollection(collectionId);
    }

    @Subscribe
    public void onLoginSuccess(LoginEvent event) {
        currentPage = 0;
        allCollections.setAdapter(null, 0);
        getFavouriteCollections();
    }

    @Subscribe
    public void onLogoutSuccess(LogoutEvent event) {
        allCollections.removeAllViews();
        currentPage = 0;
        adapter = null;
        showEmptyView();
    }
}