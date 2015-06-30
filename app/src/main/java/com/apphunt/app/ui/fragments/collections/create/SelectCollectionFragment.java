package com.apphunt.app.ui.fragments.collections.create;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetMyCollectionsEvent;
import com.apphunt.app.ui.adapters.SelectCollectionAdapter;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 6/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class SelectCollectionFragment extends Fragment {

    private Activity activity;
    private View view;
    private App app;

    @InjectView(R.id.collections_list)
    RecyclerView collectionsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_collection, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        ButterKnife.inject(this, view);

//        if(LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
//            ApiClient.getClient(activity).getMyCollections(LoginProviderFactory.get(activity).getUser().getId(), 1, 10);
            ApiClient.getClient(activity).getMyCollections("5517b7ad8dffc3030010f80b", 1, 10);
//        }

        collectionsList.setItemAnimator(new DefaultItemAnimator());
        collectionsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        collectionsList.setHasFixedSize(true);

    }

    @OnClick(R.id.add_collection)
    public void onAddCollectionClick() {
        NavUtils.getInstance((AppCompatActivity) activity).presentCreateCollectionFragment();
    }

    public void setSelectedApp(App selectedApp) {
        this.app = selectedApp;
    }

    @Subscribe
    public void onMyCollectionsReceive(GetMyCollectionsEvent event) {
        collectionsList.setAdapter(new SelectCollectionAdapter(activity, event.getAppsCollection().getCollections()));
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
