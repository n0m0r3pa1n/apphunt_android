package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetTopAppsCollectionEvent;
import com.apphunt.app.ui.adapters.TopAppsAdapter;
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

    @InjectView(R.id.collection_apps_list)
    ListView collectionAppsList;

    public TopAppsFragment() {
        setTitle(R.string.title_top_apps);
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

        ApiClient.getClient(getActivity()).getTopAppsCollection("top apps for may",
                LoginProviderFactory.get(getActivity()).getUser().getId());

        return view;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onCollectionReceived(GetTopAppsCollectionEvent event) {
        TopAppsAdapter adapter = new TopAppsAdapter(getActivity(), event.getAppsCollection().getCollections().get(0).getApps());
        collectionAppsList.setAdapter(adapter);
        Log.e(TAG, event.getAppsCollection().getCollections().get(0).toString());


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
}
