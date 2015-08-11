package com.apphunt.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;

import butterknife.ButterKnife;

/**
 * Created by nmp on 15-8-11.
 */
public class SearchResultsFragments extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }
}
