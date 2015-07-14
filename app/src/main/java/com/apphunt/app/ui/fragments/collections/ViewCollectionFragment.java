package com.apphunt.app.ui.fragments.collections;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionEvent;
import com.apphunt.app.event_bus.events.ui.collections.EditCollectionEvent;
import com.apphunt.app.ui.adapters.collections.CollectionAppsAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
import com.apphunt.app.ui.views.collection.CollectionView;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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

    @InjectView(R.id.edit_banner)
    ImageButton editBanner;
    
    @InjectView(R.id.empty_view)
    ViewStub emptyView;

    private AppsCollection appsCollection;
    private CollectionAppsAdapter collectionAppsAdapter;
    private Activity activity;

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
        List<BaseApp> apps = new ArrayList<>(appsCollection.getApps());
        collectionAppsAdapter = new CollectionAppsAdapter(activity, apps);
        collectionApps.setAdapter(collectionAppsAdapter);
        collectionView.setCollection(appsCollection, true);

        description.setText(appsCollection.getDescription());

        collectionAppsAdapter.setListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                NavUtils.getInstance((AppCompatActivity) activity).presentAppDetailsFragment(appsCollection.getApps().get(position));
            }
        });

        if (appsCollection.isOwnedByCurrentUser(activity)) {
            editCollection.setVisibility(View.VISIBLE);
        }

        if (appsCollection.getApps().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public String getStringTitle() {
        return appsCollection.getName();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem deleteCollectionAction = menu.findItem(R.id.action_delete_collection);
        if(appsCollection.isOwnedByCurrentUser(activity)) {
            deleteCollectionAction.setVisible(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_collection:
                ApiClient.getClient(activity).deleteCollection(appsCollection.getId());
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
            if(TextUtils.isEmpty(collectionView.editName.getText().toString())) {
                collectionView.editName.setError("Name can not be empty!");
                return;
            } else {
                collectionView.editName.setError(null);
            }

            if(TextUtils.isEmpty(desc)) {
                editDescription.setError("Description can not be empty!");
                return;
            } else {
                editDescription.setError(null);
            }

            editDescription.setVisibility(View.GONE);
            editBanner.setVisibility(View.GONE);
            description.setVisibility(View.VISIBLE);
            description.setText(desc);

            collectionAppsAdapter.setEditable(false);

            editCollection.setImageResource(R.drawable.btn_edit);
            editCollection.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_fab)));

            appsCollection.setDescription(desc);
            appsCollection.setName(collectionView.editName.getText().toString());
            appsCollection.setApps(collectionAppsAdapter.getItems());

            ApiClient.getClient(activity).updateCollection(LoginProviderFactory.get(activity).getUser().getId(), appsCollection);

            if (appsCollection.getApps().size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if (emptyView.getVisibility() == View.VISIBLE) {
                emptyView.setVisibility(View.GONE);
            }

            collectionAppsAdapter.setEditable(true);
            editBanner.setVisibility(View.VISIBLE);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideSoftKeyboard();
        if(isSave) {
            BusProvider.getInstance().post(new UpdateCollectionEvent(appsCollection, false));
        }
    }
}
