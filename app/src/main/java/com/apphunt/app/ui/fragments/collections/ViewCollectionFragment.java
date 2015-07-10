package com.apphunt.app.ui.fragments.collections;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.collections.EditCollectionEvent;
import com.apphunt.app.ui.adapters.collections.CollectionAppsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
import com.apphunt.app.ui.views.collection.CollectionView;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by nmp on 15-7-3.
 */
public class ViewCollectionFragment extends BaseFragment {
    private static final String APPS_COLLECTION_KEY = "AppsCollection";

    private boolean isSave;

    @InjectView(R.id.collection)
    CollectionView collectionView;

    @InjectView(R.id.collection_apps)
    RecyclerView collectionApps;

    @InjectView(R.id.description)
    TextView description;

    @InjectView(R.id.edit_description)
    EditText editDescription;

    @InjectView(R.id.edit_collection)
    FloatingActionButton editCollection;

    @InjectView(R.id.vs_no_collection)
    ViewStub vsNoCollection;

    private AppsCollection appsCollection;
    private CollectionAppsAdapter collectionAppsAdapter;

    public ViewCollectionFragment() {
    }

    public static ViewCollectionFragment newInstance(AppsCollection appsCollection) {
        ViewCollectionFragment fragment = new ViewCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(APPS_COLLECTION_KEY, appsCollection);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_collection, container, false);
        ButterKnife.inject(this, view);

        setHasOptionsMenu(true);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        collectionApps.setItemAnimator(new DefaultItemAnimator());
        collectionApps.setLayoutManager(layoutManager);
        collectionApps.setHasFixedSize(true);

        appsCollection = (AppsCollection) getArguments().getSerializable(APPS_COLLECTION_KEY);
        collectionAppsAdapter = new CollectionAppsAdapter(getActivity(), appsCollection.getApps());
        collectionApps.setAdapter(collectionAppsAdapter);
        collectionView.setCollection(appsCollection, true);

        description.setText(appsCollection.getDescription());

        collectionAppsAdapter.setListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                NavUtils.getInstance((AppCompatActivity) getActivity()).presentAppDetailsFragment(appsCollection.getApps().get(position));
            }
        });

        if(appsCollection.isOwnedByCurrentUser(getActivity())) {
            editCollection.setVisibility(View.VISIBLE);
        }

        if(appsCollection.getApps().size() == 0) {
            vsNoCollection.setVisibility(View.VISIBLE);
        }

        ActionBarUtils.getInstance().setTitle("Collection");

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem deleteCollectionAction = menu.findItem(R.id.action_delete_collection);
        if(appsCollection.isOwnedByCurrentUser(getActivity())) {
            deleteCollectionAction.setVisible(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_collection:
                ApiClient.getClient(getActivity()).deleteCollection(appsCollection.getId());
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
    }

    @OnClick(R.id.edit_collection)
    public void editCollection() {
        if(isSave) {
            String desc = editDescription.getText().toString();
            editDescription.setVisibility(View.GONE);
            description.setText(desc);

            collectionAppsAdapter.setEditable(false);

            editCollection.setImageResource(R.drawable.btn_edit);
            editCollection.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_fab)));

            appsCollection.setDescription(desc);
            appsCollection.setName(collectionView.editName.getText().toString());
            appsCollection.setApps(collectionAppsAdapter.getItems());

            ApiClient.getClient(getActivity()).updateCollection(LoginProviderFactory.get(getActivity()).getUser().getId(), appsCollection);
        } else {
            collectionAppsAdapter.setEditable(true);
            description.setVisibility(View.GONE);
            editDescription.setText(appsCollection.getDescription());
            editDescription.setVisibility(View.VISIBLE);
            BusProvider.getInstance().post(new EditCollectionEvent(appsCollection.getId()));
            editCollection.setImageResource(R.drawable.btn_ok);
            editCollection.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.save_collection_color)));
        }

        isSave = !isSave;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().setPreviousTitle();
    }
}
