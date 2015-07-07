package com.apphunt.app.ui.fragments.collections;

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
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.adapters.collections.CollectionAppsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.views.CollectionView;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-7-3.
 */
public class ViewCollectionFragment extends BaseFragment {
    private static final String APPS_COLLECTION_KEY = "AppsCollection";

    @InjectView(R.id.collection)
    CollectionView collection;

    @InjectView(R.id.collection_apps)
    GridView collectionApps;

    @InjectView(R.id.edit_collection)
    FloatingActionButton editCollection;

    private AppsCollection appsCollection;

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
        collection.setCollection(appsCollection, true);
        collectionApps.setAdapter(new CollectionAppsAdapter(getActivity(), appsCollection.getApps()));
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

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().setPreviousTitle();
    }
}
