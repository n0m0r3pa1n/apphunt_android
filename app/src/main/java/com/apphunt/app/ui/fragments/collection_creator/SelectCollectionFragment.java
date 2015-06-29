package com.apphunt.app.ui.fragments.collection_creator;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.adapters.SelectCollectionAdapter;
import com.apphunt.app.utils.ui.NavUtils;

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

        // TODO: Test reason
        ArrayList<AppsCollection> appsCollections = new ArrayList<>();
        AppsCollection col1 = new AppsCollection();
        col1.setName("Test 1");
        appsCollections.add(col1);

        AppsCollection col2 = new AppsCollection();
        col2.setName("Test 2");
        appsCollections.add(col2);

        collectionsList.setAdapter(new SelectCollectionAdapter(activity, appsCollections));
    }

    @OnClick(R.id.add_collection)
    public void onAddCollectionClick() {
        NavUtils.getInstance((AppCompatActivity) activity).presentCreateCollectionFragment();
    }

    public void setSelectedApp(App selectedApp) {
        this.app = selectedApp;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
