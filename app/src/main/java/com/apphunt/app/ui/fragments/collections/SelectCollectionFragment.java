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
import android.widget.ListView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.StatusCode;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetMyCollectionsEvent;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionEvent;
import com.apphunt.app.ui.adapters.SelectCollectionAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
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

    @InjectView(R.id.my_collections_container)
    RecyclerView myCollections;

    private SelectCollectionAdapter selectCollectionAdapter;
    private BaseApp app;

    public static SelectCollectionFragment newInstance(App app) {
        SelectCollectionFragment fragment = new SelectCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(APP_KEY, app);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_collection, container, false);
        ButterKnife.inject(this, view);
        ApiClient.getClient(getActivity()).getMyCollections(LoginProviderFactory.get(getActivity()).getUser().getId(), 1, 5);

        app = (BaseApp) getArguments().getSerializable(APP_KEY);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myCollections.setLayoutManager(layoutManager);
        myCollections.setHasFixedSize(true);

        return view;
    }

    @Subscribe
    public void onMyCollectionsReceive(GetMyCollectionsEvent event) {
        selectCollectionAdapter = new SelectCollectionAdapter(getActivity(), event.getAppsCollection().getCollections());
        selectCollectionAdapter.setOnItemClickListener(this);
        myCollections.setAdapter(selectCollectionAdapter);
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
            ApiClient.getClient(getActivity()).updateCollection(selectCollectionAdapter.getCollectionId(position),
                    new String[]{app.getId()});
        }
    }

    @Subscribe
    public void onUpdateCollection(UpdateCollectionEvent event) {
        if(event.getStatusCode() == StatusCode.SUCCESS.getCode()) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}