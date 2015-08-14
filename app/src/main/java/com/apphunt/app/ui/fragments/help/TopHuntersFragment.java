package com.apphunt.app.ui.fragments.help;

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
public class TopHuntersFragment extends BaseFragment {
    public TopHuntersFragment() {
        setTitle(R.string.top_hunters_points);
        FlurryAgent.logEvent(TrackingEvents.UserViewedHelpTopHuntersPoints);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_top_hunters_points, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public int getTitle() {
        return R.string.top_hunters_points;
    }
}
