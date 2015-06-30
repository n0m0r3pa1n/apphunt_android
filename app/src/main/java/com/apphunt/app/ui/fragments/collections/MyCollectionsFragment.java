package com.apphunt.app.ui.fragments.collections;

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
import com.apphunt.app.event_bus.events.api.collections.GetMyCollectionsEvent;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.listeners.EndlessScrollListener;
import com.apphunt.app.ui.views.ScrollListView;
import com.apphunt.app.utils.Constants;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-26.
 */
public class MyCollectionsFragment extends BaseFragment {

    int currentPage = 0;

    private CollectionsAdapter adapter;

    @InjectView(R.id.all_collections)
    ScrollListView allCollections;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getMyCollections();

        View view = inflater.inflate(R.layout.fragment_all_collections, container, false);
        ButterKnife.inject(this, view);

        allCollections.setOnEndReachedListener(new EndlessScrollListener.OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getMyCollections();
            }
        });
        return view;
    }

    private void getMyCollections() {
        if(LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            currentPage++;
            ApiClient.getClient(getActivity()).getMyCollections(LoginProviderFactory.get(getActivity()).getUser().getId(),
                    currentPage, Constants.PAGE_SIZE);
        }
    }

    @Subscribe
    public void onMyCollectionsReceived(GetMyCollectionsEvent event) {
        allCollections.hideBottomLoader();
        if(adapter == null) {
            adapter = new CollectionsAdapter(R.layout.layout_my_collection_item,
                    event.getAppsCollection().getCollections());
            allCollections.setAdapter(adapter, event.getAppsCollection().getTotalCount());
        } else {
            int currentSize = adapter.getCount();
            adapter.addAllCollections(event.getAppsCollection().getCollections());
            allCollections.smoothScrollToPosition(currentSize);
        }
    }

    @Override
    public int getTitle() {
        return R.string.title_my_collection;
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
