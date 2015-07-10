package com.apphunt.app.ui.fragments.collections.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.DeleteCollectionEvent;
import com.apphunt.app.event_bus.events.api.collections.GetAllCollectionsEvent;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.listeners.EndlessScrollListener;
import com.apphunt.app.ui.views.ScrollListView;
import com.apphunt.app.constants.Constants;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-26.
 */
public class AllCollectionsFragment extends BaseFragment {
    public static final String TAG = AllCollectionsFragment.class.getSimpleName();

    @InjectView(R.id.all_collections)
    ScrollListView allCollections;

    private CollectionsAdapter adapter;
    private int currentPage = 0;

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
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }


}
