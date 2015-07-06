package com.apphunt.app.ui.fragments.collections;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.views.CollectionView;
import com.apphunt.app.utils.ui.ActionBarUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-7-3.
 */
public class CollectionDetailsFragment extends BaseFragment {
    private static final String APPS_COLLECTION_KEY = "AppsCollection";

    @InjectView(R.id.collection)
    CollectionView collection;

    @InjectView(R.id.collection_apps)
    GridView collectionApps;

    private AppsCollection appsCollection;

    public CollectionDetailsFragment() {
    }

    public static CollectionDetailsFragment newInstance(AppsCollection appsCollection) {
        CollectionDetailsFragment fragment = new CollectionDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(APPS_COLLECTION_KEY, appsCollection);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_details, container, false);
        ButterKnife.inject(this, view);

        collection.setCollection((AppsCollection) getArguments().getSerializable(APPS_COLLECTION_KEY));

        ActionBarUtils.getInstance().setTitle("Collection");

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().setPreviousTitle();
    }
}
