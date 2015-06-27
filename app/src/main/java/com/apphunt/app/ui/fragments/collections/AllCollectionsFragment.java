package com.apphunt.app.ui.fragments.collections;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetAllCollectionsEvent;
import com.apphunt.app.ui.adapters.CollectionsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-26.
 */
public class AllCollectionsFragment extends BaseFragment {

    @InjectView(R.id.all_collections)
    ListView allCollections;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            ApiClient.getClient(getActivity()).getAllCollections(LoginProviderFactory.get(getActivity()).getUser().getId(), 1, 5);
        } else {
            ApiClient.getClient(getActivity()).getAllCollections(null, 1, 5);
        }

        View view = inflater.inflate(R.layout.fragment_all_collections, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Subscribe
    public void onAllCollectionsReceived(GetAllCollectionsEvent event) {
        allCollections.setAdapter(new CollectionsAdapter(event.getAppsCollection().getCollections()));
    }

    @Override
    public int getTitle() {
        return R.string.title_all_collection;
    }
}
