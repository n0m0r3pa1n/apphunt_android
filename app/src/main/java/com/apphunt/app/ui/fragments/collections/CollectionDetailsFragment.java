package com.apphunt.app.ui.fragments.collections;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.fragments.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by nmp on 15-7-3.
 */
public class CollectionDetailsFragment extends BaseFragment {

    private AppsCollection appsCollection;

    public CollectionDetailsFragment() {
    }

    public CollectionDetailsFragment(AppsCollection appsCollection) {
        this.appsCollection = appsCollection;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_details, container, false);
        ButterKnife.inject(this, view);


        return view;
    }
}
