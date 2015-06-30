package com.apphunt.app.ui.fragments.collections;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.FavouriteCollectionEvent;
import com.apphunt.app.event_bus.events.api.collections.GetFavouriteCollectionsEvent;
import com.apphunt.app.event_bus.events.api.collections.UnfavouriteCollectionEvent;
import com.apphunt.app.ui.adapters.CollectionsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-26.
 */
public class FavouriteCollectionsFragment extends BaseFragment {
    public static final String TAG = FavouriteCollectionsFragment.class.getSimpleName();
    private CollectionsAdapter adapter;
    @InjectView(R.id.all_collections)
    ListView allCollections;

    @Override
    public int getTitle() {
        return R.string.title_favourite_collection;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getFavouriteCollections();

        View view  = inflater.inflate(R.layout.fragment_all_collections, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    private void getFavouriteCollections() {
        Log.d(TAG, "getFavouriteCollections ");
        if(LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            ApiClient.getClient(getActivity()).getFavouriteCollections(
                    LoginProviderFactory.get(getActivity()).getUser().getId(), 1, 5);
        }
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

    @Subscribe
    public void onCollectionFavourited(FavouriteCollectionEvent event) {
        Log.d(TAG, "onCollectionFavourited ");
        getFavouriteCollections();
    }

    @Subscribe
    public void onCollectionUnfavourited(UnfavouriteCollectionEvent event) {
        Log.d(TAG, "onCollectionUnfavourited ");
        adapter.removeCollection(event.getCollectionId());
    }

    @Subscribe
    public void onFavouriteCollectionReceived(GetFavouriteCollectionsEvent event) {
        Log.d(TAG, "onFavouriteCollectionReceived ");
        if(adapter == null) {
            adapter = new CollectionsAdapter(event.getAppsCollection().getCollections());
            allCollections.setAdapter(adapter);
        } else {
            adapter.updateData(event.getAppsCollection().getCollections());
        }
    }
}
