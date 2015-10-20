package com.apphunt.app.ui.fragments.help;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.constants.TrackingEvents;
import com.flurry.android.FlurryAgent;

import butterknife.ButterKnife;

/**
 * Created by nmp on 15-6-9.
 */
public class AddAppFragment extends BaseFragment {
    private Activity activity;

    public AddAppFragment() {
        setTitle(R.string.how_to_add_new_app);
        FlurryAgent.logEvent(TrackingEvents.UserViewedHelpAddApp);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public int getTitle() {
        return R.string.how_to_add_new_app;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_add_app, container, false);
        ButterKnife.inject(this, view);

        return view;
    }
}
