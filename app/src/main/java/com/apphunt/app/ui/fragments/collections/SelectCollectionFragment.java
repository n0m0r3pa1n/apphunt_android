package com.apphunt.app.ui.fragments.collections;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.client.AppHuntApiClient;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.utils.ui.NavUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by nmp on 15-7-4.
 */
public class SelectCollectionFragment extends BaseFragment {
    public static final String TAG = SelectCollectionFragment.class.getSimpleName();


    public static SelectCollectionFragment newInstance(App app) {
        SelectCollectionFragment fragment = new SelectCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("App", app);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_collection, container, false);
        MyCollectionsFragment fragment = new MyCollectionsFragment();
        fragment.setSelectedApp((App) getArguments().getSerializable("App"));
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.my_collections_container, fragment).commit();
        return view;
    }

    @OnClick(R.id.add_collection)
    public void openAddCollectionFragment() {
        NavUtils.getInstance((AppCompatActivity) getActivity()).presentCreateCollectionFragment();
    }
}