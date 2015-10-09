package com.apphunt.app.ui.fragments.collections.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.DeleteCollectionApiEvent;
import com.apphunt.app.event_bus.events.api.collections.FavouriteCollectionApiEvent;
import com.apphunt.app.event_bus.events.api.collections.GetFavouriteCollectionsApiEvent;
import com.apphunt.app.event_bus.events.api.collections.UnfavouriteCollectionApiEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollListView;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-26.
 */
public class FavouriteCollectionsFragment extends BaseFragment {
    public static final String TAG = FavouriteCollectionsFragment.class.getSimpleName();
    public static final String FAVOURITED_BY = "FAVOURITED_BY";

    private CollectionsAdapter adapter;
    private int currentPage = 0;
    private String favouriteBy;

    @InjectView(R.id.all_collections)
    ScrollListView allCollections;

    @InjectView(R.id.vs_no_collection)
    ViewStub vsNoCollection;

    View view;

    @Override
    public int getTitle() {
        return R.string.title_favourite_collection;
    }

    public static FavouriteCollectionsFragment newInstance(String profileId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USER_PROFILE, profileId);
        FavouriteCollectionsFragment fragment = new FavouriteCollectionsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            FlurryAgent.logEvent(TrackingEvents.UserViewedFavouriteCollections);
        view  = inflater.inflate(R.layout.fragment_all_collections, container, false);
        ButterKnife.inject(this, view);

        favouriteBy = getArguments().getString(Constants.KEY_USER_PROFILE);
        getFavouriteCollections();

        allCollections.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getFavouriteCollections();
            }
        });
        return view;
    }

    private void getFavouriteCollections() {
        currentPage++;
        if (LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            ApiClient.getClient(getActivity()).getFavouriteCollections(favouriteBy,
                    LoginProviderFactory.get(getActivity()).getUser().getId(), currentPage, Constants.PAGE_SIZE);
            hideEmptyView();
            return;
        } else if(TextUtils.isEmpty(favouriteBy)) {
            showEmptyView();
            return;
        }

        ApiClient.getClient(getActivity()).getFavouriteCollections(favouriteBy, null, currentPage, Constants.PAGE_SIZE);
    }

    private void hideEmptyView() {
        vsNoCollection.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        vsNoCollection.setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.score_text)).setText(getResources().getString(R.string.no_favourite_collections));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(FAVOURITED_BY, favouriteBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onCollectionFavourited(FavouriteCollectionApiEvent event) {
        if (adapter != null) {
            adapter.addCollection(event.getCollection());
            if(adapter.getCount() > 0) {
                hideEmptyView();
            }
        }
    }

    @Subscribe
    public void onCollectionUnfavourited(UnfavouriteCollectionApiEvent event) {
        if (adapter != null && favouriteBy.equals(LoginProviderFactory.get(getActivity()).getUser().getId())) {
            adapter.removeCollection(event.getCollectionId());
            if(adapter.getCount() == 0) {
                showEmptyView();
            }
        }
    }

    @Subscribe
    public void onFavouriteCollectionReceived(GetFavouriteCollectionsApiEvent event) {
        allCollections.hideBottomLoader();

        if (adapter == null) {
            adapter = new CollectionsAdapter(getActivity(), event.getAppsCollection().getCollections());
            allCollections.setAdapter(adapter, event.getAppsCollection().getTotalCount());
        } else {
            int currentSize = adapter.getCount();
            adapter.addAllCollections(event.getAppsCollection().getCollections());
            allCollections.smoothScrollToPosition(currentSize);
        }

        if (event.getAppsCollection().getCollections().size() == 0) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    @Subscribe
    public void onCollectionDeleted(DeleteCollectionApiEvent event) {
        String collectionId = event.getCollectionId();
        adapter.removeCollection(collectionId);
    }

    @Subscribe
    public void onLoginSuccess(LoginEvent event) {
        currentPage = 0;
        allCollections.setAdapter(null, 0);
        getFavouriteCollections();
    }

    @Subscribe
    public void onLogoutSuccess(LogoutEvent event) {
        allCollections.removeAllViews();
        currentPage = 0;
        adapter = null;
        showEmptyView();
    }
}
