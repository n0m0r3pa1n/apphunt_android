package com.apphunt.app.ui.fragments.collection_creator;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.utils.ui.AddToCollectionUtils;

import butterknife.ButterKnife;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_collection, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        ButterKnife.inject(this, view);
    }

    @OnClick(R.id.add_collection)
    public void onAddCollectionClick() {
        AddToCollectionUtils.getInstance().presentCreateCollectionFragment((FragmentActivity) activity);
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
