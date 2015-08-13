package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetTopHuntersCollectionApiEvent;
import com.apphunt.app.ui.adapters.rankings.TopHuntersAdapter;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 5/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class TopHuntersFragment extends BaseFragment {

    private static final String TAG = TopHuntersFragment.class.getSimpleName();
    private Activity activity;
    private String title;

    @InjectView(R.id.collection_hunters_list)
    RecyclerView collectionHuntersList;

    public TopHuntersFragment() {
        setFragmentTag(Constants.TAG_TOP_HUNTERS_FRAGMENT);
        FlurryAgent.logEvent(TrackingEvents.UserViewedTopHunters);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiClient.getClient(activity).getTopHuntersCollection(StringUtils.getMonthStringFromCalendar(1));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_hunters, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onCollectionReceived(GetTopHuntersCollectionApiEvent event) {
        collectionHuntersList.setItemAnimator(new DefaultItemAnimator());
        collectionHuntersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        collectionHuntersList.setHasFixedSize(true);
        collectionHuntersList.setAdapter(new TopHuntersAdapter(activity, event.getHuntersCollections().getCollections().get(0)));
        title = event.getHuntersCollections().getCollections().get(0).getName();
        ActionBarUtils.getInstance().setTitle(title);
    }

    @Override
    public String getStringTitle() {
        return title;
    }
}
