package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetTopAppsCollectionEvent;
import com.apphunt.app.ui.adapters.rankings.TopAppsAdapter;
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
public class TopAppsFragment extends BaseFragment {

    private static final String TAG = TopAppsFragment.class.getSimpleName();
    private Activity activity;
    private View view;
    private StaggeredGridLayoutManager layoutManager;
    private String title;

    @InjectView(R.id.collection_apps_list)
    RecyclerView collectionAppsList;

    public TopAppsFragment() {
        setFragmentTag(Constants.TAG_TOP_APPS_FRAGMENT);
        FlurryAgent.logEvent(TrackingEvents.UserViewedTopApps);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_apps, container, false);
        ButterKnife.inject(this, view);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        collectionAppsList.setItemAnimator(new DefaultItemAnimator());
        collectionAppsList.setLayoutManager(layoutManager);
        collectionAppsList.setHasFixedSize(true);

        ApiClient.getClient(activity).getTopAppsCollection(StringUtils.getMonthStringFromCalendar(1), LoginProviderFactory.get(getActivity()).getUser().getId());

        return view;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onCollectionReceived(GetTopAppsCollectionEvent event) {
        TopAppsAdapter adapter = new TopAppsAdapter(getActivity(), event.getAppsCollection().getCollections().get(0).getApps());
        collectionAppsList.setAdapter(adapter);
        title = event.getAppsCollection().getCollections().get(0).getName();
        ActionBarUtils.getInstance().setTitle(title);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public String getStringTitle() {
        return title;
    }
}
