package com.apphunt.app.ui.fragments.profile.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.ui.fragments.base.BaseFragment;

/**
 * Created by nmp on 15-8-18.
 */
public class CommentsFragment extends BaseFragment {

    public static final String USER_ID = "USER_ID";
    private AppCompatActivity activity;
    private String userId;

    public static CommentsFragment newInstance(String userId) {

        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_comments, container, false);
        userId = getArguments().getString(USER_ID);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }
}
