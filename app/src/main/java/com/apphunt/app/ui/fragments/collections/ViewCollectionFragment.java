package com.apphunt.app.ui.fragments.collections;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.collections.EditCollectionEvent;
import com.apphunt.app.event_bus.events.ui.collections.SaveCollectionEvent;
import com.apphunt.app.ui.adapters.collections.CollectionAppsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.views.collection.CollectionView;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by nmp on 15-7-3.
 */
public class ViewCollectionFragment extends BaseFragment {
    private static final String APPS_COLLECTION_KEY = "AppsCollection";

    private boolean isEdit;

    @InjectView(R.id.collection)
    CollectionView collection;

    @InjectView(R.id.collection_apps)
    GridView collectionApps;

    @InjectView(R.id.edit_collection)
    FloatingActionButton editCollection;

    private AppsCollection appsCollection;
    private CollectionAppsAdapter collectionAppsAdapter;

    public ViewCollectionFragment() {
    }

    public static ViewCollectionFragment newInstance(AppsCollection appsCollection) {
        ViewCollectionFragment fragment = new ViewCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(APPS_COLLECTION_KEY, appsCollection);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_collection, container, false);
        ButterKnife.inject(this, view);

        appsCollection = (AppsCollection) getArguments().getSerializable(APPS_COLLECTION_KEY);
        collectionAppsAdapter = new CollectionAppsAdapter(getActivity(), appsCollection.getApps());
        collectionApps.setAdapter(collectionAppsAdapter);
        collection.setCollection(appsCollection, true);
        collectionApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavUtils.getInstance((AppCompatActivity) getActivity()).presentAppDetailsFragment(appsCollection.getApps().get(position));
            }
        });

        if(appsCollection.isOwnedByCurrentUser(getActivity())) {
            editCollection.setVisibility(View.VISIBLE);
        }

        ActionBarUtils.getInstance().setTitle("Collection");

        return view;
    }

    @OnClick(R.id.edit_collection)
    public void editCollection() {
        if(isEdit) {
            collectionAppsAdapter.setEditable(false);
            BusProvider.getInstance().post(new SaveCollectionEvent());
            editCollection.setImageResource(R.drawable.btn_edit);
            editCollection.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_fab)));
            appsCollection.setName(collection.getCollection().getName());
            appsCollection.setApps(collection.getCollection().getApps());
            ApiClient.getClient(getActivity()).updateCollection(appsCollection);
        } else {
            collectionAppsAdapter.setEditable(true);
            BusProvider.getInstance().post(new EditCollectionEvent());
            editCollection.setImageResource(R.drawable.btn_ok);
            editCollection.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.save_collection_color)));
        }

        isEdit = !isEdit;


    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().setPreviousTitle();
    }
}
