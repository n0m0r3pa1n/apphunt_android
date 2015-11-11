package com.apphunt.app.ui.fragments.collections.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.DeleteCollectionApiEvent;
import com.apphunt.app.event_bus.events.api.collections.GetAllCollectionsApiEvent;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionApiEvent;
import com.apphunt.app.ui.adapters.collections.CollectionsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollListView;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AllCollectionsFragment extends BaseFragment {
    public static final String TAG = AllCollectionsFragment.class.getSimpleName();

    @InjectView(R.id.all_collections)
    ScrollListView allCollections;

    private int currentPage = 0;
    private String previousSelectedSortItem = "all";
    private String userId = null;
    private CollectionsAdapter adapter;

    public static AllCollectionsFragment newInstance() {
        return new AllCollectionsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FlurryWrapper.logEvent(TrackingEvents.UserViewedAllCollections);
        View view = inflater.inflate(R.layout.fragment_all_collections, container, false);
        ButterKnife.inject(this, view);
        loadMoreCollections(previousSelectedSortItem);
        allCollections.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                loadMoreCollections(previousSelectedSortItem);
            }
        });

        return view;
    }

    private void loadMoreCollections(String sortBy) {
        String userId = null;
        currentPage++;
        if(LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            userId = LoginProviderFactory.get(getActivity()).getUser().getId();
        }
        ApiClient.getClient(getActivity()).getAllCollections(userId, sortBy, currentPage, Constants.PAGE_SIZE);
    }

    @Override
    public int getTitle() {
        return R.string.title_all_collection;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(getActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
            MenuItem sortMenuItem = menu.findItem(R.id.action_sort).setVisible(true);
            Spinner spinner = (Spinner) sortMenuItem.getActionView().findViewById(R.id.sort_by);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(!isAdded()) {
                        return;
                    }
                    String[] items = getResources().getStringArray(R.array.order_values);
                    String selectedItem = items[position];
                    if (previousSelectedSortItem.equals(selectedItem)) {
                        return;
                    }

                    currentPage = 0;
                    adapter = null;
                    allCollections.resetListView();

                    previousSelectedSortItem = selectedItem;
                    loadMoreCollections(selectedItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Subscribe
    public void onCollectionsReceived(GetAllCollectionsApiEvent event) {
        if(adapter == null) {
            adapter = new CollectionsAdapter(getActivity(), event.getAppsCollection().getCollections());
            allCollections.setAdapter(adapter, event.getAppsCollection().getTotalCount());
        } else {
            int currentSize = adapter.getCount();
            adapter.addAllCollections(event.getAppsCollection().getCollections());
        }
    }

    @Subscribe
    public void onCollectionDeleted(DeleteCollectionApiEvent event) {
        String collectionId = event.getCollectionId();
        adapter.removeCollection(collectionId);
    }

    @Subscribe
    public void onCollectionEdit(UpdateCollectionApiEvent event) {
        if(!event.isSuccess()) {
            return;
        }

        currentPage = 0;
        adapter = null;
        allCollections.resetListView();
        loadMoreCollections(previousSelectedSortItem);
    }
}
