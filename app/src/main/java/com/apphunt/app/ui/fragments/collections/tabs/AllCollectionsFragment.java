package com.apphunt.app.ui.fragments.collections.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.DeleteCollectionEvent;
import com.apphunt.app.event_bus.events.api.collections.GetAllCollectionsEvent;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionEvent;
import com.apphunt.app.event_bus.events.ui.collections.EditCollectionEvent;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.listeners.EndlessScrollListener;
import com.apphunt.app.ui.views.ScrollListView;
import com.apphunt.app.constants.Constants;
import com.google.android.gms.common.api.Api;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AllCollectionsFragment extends BaseFragment {
    public static final String TAG = AllCollectionsFragment.class.getSimpleName();

    @InjectView(R.id.all_collections)
    ScrollListView allCollections;

    private int currentPage = 0;
    private String userId = null;
    private CollectionsAdapter adapter;
    private List<AppsCollection> appCollections;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadMoreCollections();

        View view = inflater.inflate(R.layout.fragment_all_collections, container, false);
        ButterKnife.inject(this, view);
        allCollections.setOnEndReachedListener(new EndlessScrollListener.OnEndReachedListener() {
            @Override
            public void onEndReached() {
                loadMoreCollections();
            }
        });

        return view;
    }

    private void loadMoreCollections() {
        String userId = null;
        currentPage++;
        if(LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            userId = LoginProviderFactory.get(getActivity()).getUser().getId();
        }
        ApiClient.getClient(getActivity()).getAllCollections(userId, currentPage, Constants.PAGE_SIZE);
    }

    @Subscribe
    public void onCollectionsReceived(GetAllCollectionsEvent event) {
        appCollections = event.getAppsCollection().getCollections();
        allCollections.hideBottomLoader();
        if(adapter == null) {
            adapter = new CollectionsAdapter(event.getAppsCollection().getCollections());
            allCollections.setAdapter(adapter, event.getAppsCollection().getTotalCount());
        } else {
            int currentSize = adapter.getCount();
            adapter.addAllCollections(event.getAppsCollection().getCollections());
            allCollections.smoothScrollToPosition(currentSize);
        }
    }

    @Subscribe
    public void onCollectionDeleted(DeleteCollectionEvent event) {
        String collectionId = event.getCollectionId();
        adapter.removeCollection(collectionId);
    }

    @Subscribe
    public void onCollectionEdit(UpdateCollectionEvent event) {
        currentPage = 0;
        adapter = null;
        allCollections.resetListView();
        loadMoreCollections();
    }

    @Override
    public int getTitle() {
        return R.string.title_all_collection;
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
}
