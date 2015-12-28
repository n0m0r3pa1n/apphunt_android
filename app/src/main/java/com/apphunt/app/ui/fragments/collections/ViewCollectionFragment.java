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
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetCollectionApiEvent;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionApiEvent;
import com.apphunt.app.event_bus.events.ui.collections.EditCollectionEvent;
import com.apphunt.app.ui.adapters.collections.CollectionAppsAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.interfaces.OnActionNeeded;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
import com.apphunt.app.ui.views.collection.CollectionView;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.SoundsUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by nmp on 15-7-3.
 */
public class ViewCollectionFragment extends BackStackFragment {
    private static final String APPS_COLLECTION_KEY = "AppsCollection";
    private static final String APPS_COLLECTION_ID = "AppsCollectionId";

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
    private AppCompatActivity activity;
    private String title = "";

    public ViewCollectionFragment() {
    }

    public static ViewCollectionFragment newInstance(AppsCollection appsCollection) {
        ViewCollectionFragment fragment = new ViewCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(APPS_COLLECTION_KEY, appsCollection);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ViewCollectionFragment newInstance(String collectionId) {
        ViewCollectionFragment fragment = new ViewCollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(APPS_COLLECTION_ID, collectionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_collection, container, false);
        ButterKnife.inject(this, view);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        collectionApps.setItemAnimator(new DefaultItemAnimator());
        collectionApps.setLayoutManager(layoutManager);
        collectionApps.setHasFixedSize(true);

        appsCollection = (AppsCollection) getArguments().getSerializable(APPS_COLLECTION_KEY);
        if(appsCollection == null) {
            final String collectionId = getArguments().getString(APPS_COLLECTION_ID);
            FlurryWrapper.logEvent(TrackingEvents.UserViewedCollection, new HashMap<String, String>(){{
                put("collectionId", collectionId);
            }});
            String userId = LoginProviderFactory.get(activity).isUserLoggedIn() ? LoginProviderFactory.get(activity).getUser().getId() : null;
            ApiClient.getClient(activity).getAppCollection(collectionId, userId);
        } else {
            FlurryWrapper.logEvent(TrackingEvents.UserViewedCollection, new HashMap<String, String>(){{
                put("collectionId", appsCollection.getId());
            }});
            setupAppsCollection();
        }

        return view;
    }

    private void setupAppsCollection() {
        title = appsCollection.getName();
        ActionBarUtils.getInstance().setTitle(title);
        List<BaseApp> apps = new ArrayList<>(appsCollection.getApps());
        collectionAppsAdapter = new CollectionAppsAdapter(activity, apps);
        collectionApps.setAdapter(collectionAppsAdapter);
        collectionView.setCollection(appsCollection, true);

        description.setText(appsCollection.getDescription());

        collectionAppsAdapter.setListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                FlurryWrapper.logEvent(TrackingEvents.UserViewedCollectionApp);
                NavUtils.getInstance(activity).presentAppDetailsFragment(appsCollection.getApps().get(position).getId());
            }
        });

        if (appsCollection.isOwnedByCurrentUser(activity)) {
            editCollection.setVisibility(View.VISIBLE);
        }

        if (appsCollection.getApps().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getStringTitle() {
        return title;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem deleteCollectionAction = menu.findItem(R.id.action_delete_collection);
        if(appsCollection != null && appsCollection.isOwnedByCurrentUser(activity) && activity.getSupportFragmentManager().getBackStackEntryCount() == 1) {
            deleteCollectionAction.setVisible(true);
        } else {
            deleteCollectionAction.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_collection:
                FlurryWrapper.logEvent(TrackingEvents.UserDeleteCollection);
                NotificationsUtils.showNotificationFragmentWithContinueAction(activity, "Are you sure you want to delete this collection?", new OnActionNeeded() {
                    @Override
                    public void onContinueAction() {
                        ApiClient.getClient(activity).deleteCollection(appsCollection.getId());
                    }
                });
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.edit_collection)
    public void editCollection(View view) {
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
                FlurryWrapper.logEvent(TrackingEvents.UserTriedToCreateCollectionWithEmptyDesc);
                return;
            } else {
                editDescription.setError(null);
            }

            FlurryWrapper.logEvent(TrackingEvents.UserEditCollection);
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

            hideSoftKeyboard();

            ApiClient.getClient(activity).updateCollection(LoginProviderFactory.get(activity).getUser().getId(), appsCollection);
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
        SoundsUtils.performHapticFeedback(view);
    }

    @Subscribe
    public void onUpdateCollection(UpdateCollectionApiEvent event) {
        if (appsCollection.getApps().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onLoadCollection(GetCollectionApiEvent event) {
        appsCollection = event.getResponse();
        setupAppsCollection();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideSoftKeyboard();
        if(isSave) {
            FlurryWrapper.logEvent(TrackingEvents.UserDidntSaveCollection);
            BusProvider.getInstance().post(new UpdateCollectionApiEvent(appsCollection, false));
        }
    }
}
