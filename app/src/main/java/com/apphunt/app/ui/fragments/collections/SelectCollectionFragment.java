package com.apphunt.app.ui.fragments.collections;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.StatusCode;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.CreateCollectionEvent;
import com.apphunt.app.event_bus.events.api.collections.GetMyAvailableCollectionsEvent;
import com.apphunt.app.event_bus.events.api.collections.GetMyCollectionsEvent;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionEvent;
import com.apphunt.app.ui.adapters.SelectCollectionAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by nmp on 15-7-4.
 */
public class SelectCollectionFragment extends BaseFragment implements OnItemClickListener {
    public static final String TAG = SelectCollectionFragment.class.getSimpleName();

    private static final String APP_KEY = "App";
    private int currentPage = 0;

    @InjectView(R.id.my_collections_container)
    ScrollRecyclerView myCollections;

    @InjectView(R.id.vs_no_collection)
    ViewStub vsNoCollection;

    private SelectCollectionAdapter selectCollectionAdapter;
    private BaseApp app;

    public static SelectCollectionFragment newInstance(App app) {
        SelectCollectionFragment fragment = new SelectCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(APP_KEY, app);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getTitle() {
        return R.string.title_select_collection;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_collection, container, false);
        ButterKnife.inject(this, view);

        app = (BaseApp) getArguments().getSerializable(APP_KEY);
        getMyAvailableCollections();
        myCollections.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getMyAvailableCollections();
            }
        });

        return view;
    }

    private void getMyAvailableCollections() {
        currentPage++;
        if(app == null || !LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            vsNoCollection.setVisibility(View.VISIBLE);
            return;
        } else {
            vsNoCollection.setVisibility(View.GONE);
        }

        ApiClient.getClient(getActivity())
                .getMyAvailableCollections(LoginProviderFactory.get(getActivity()).getUser().getId(), app.getId(), currentPage,
                        Constants.PAGE_SIZE);
    }

    @Subscribe
    public void onMyCollectionsReceive(GetMyAvailableCollectionsEvent event) {
        myCollections.hideBottomLoader();

        if(selectCollectionAdapter == null) {
            selectCollectionAdapter = new SelectCollectionAdapter(getActivity(), event.getAppsCollection().getCollections());
            selectCollectionAdapter.setOnItemClickListener(this);
            myCollections.setAdapter(selectCollectionAdapter, event.getAppsCollection().getTotalCount());
        } else {
            selectCollectionAdapter.addAllCollections(event.getAppsCollection().getCollections());
        }

        if(event.getAppsCollection().getTotalCount() == 0) {
            vsNoCollection.setVisibility(View.VISIBLE);
        } else {
            vsNoCollection.setVisibility(View.GONE);
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



    @OnClick(R.id.add_collection)
    public void openAddCollectionFragment() {
        NavUtils.getInstance((AppCompatActivity) getActivity()).presentCreateCollectionFragment();
    }

    @Override
    public void onClick(View view, int position) {
        if(app != null) {
            AppsCollection appsCollection = selectCollectionAdapter.getCollection(position);
            appsCollection.getApps().add(app);
            ApiClient.getClient(getActivity()).updateCollection(LoginProviderFactory.get(getActivity()).getUser().getId(),
                    appsCollection);
        }
    }

    @Subscribe
    public void onUpdateCollection(UpdateCollectionEvent event) {
        if(event.getAppsCollection() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Subscribe
    public void onCollectionCreated(CreateCollectionEvent event) {
        currentPage = 0;
        selectCollectionAdapter = null;
        myCollections.resetAdapter();
        getMyAvailableCollections();
    }
}