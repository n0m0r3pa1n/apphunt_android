package com.apphunt.app.ui.fragments;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.Packages;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.PackagesFilteredApiEvent;
import com.apphunt.app.event_bus.events.ui.AppSubmittedEvent;
import com.apphunt.app.ui.adapters.InstalledAppsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.navigation.NavigationDrawerFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.listeners.EndlessScrollListener;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.PackagesUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class SelectAppFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectAppFragment.class.getName();
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private int totalPages = -1;
    private List<ApplicationInfo> appInfos = new ArrayList<>();

    @InjectView(R.id.loading)
    CircularProgressBar loader;

    @InjectView(R.id.gv_apps_list)
    GridView gridView;

    @InjectView(R.id.info)
    TextView info;

    @InjectView(R.id.no_apps)
    ImageView noAppsView;

    private View view;
    private InstalledAppsAdapter userAppsAdapter;
    private AppCompatActivity activity;

    private EndlessScrollListener endlessScrollListener = new EndlessScrollListener(new OnEndReachedListener() {
        @Override
        public void onEndReached() {
            FlurryWrapper.logEvent(TrackingEvents.UserScrolledDownSelectAppsList);
            filterApps();
        }
    });


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appInfos = PackagesUtils.getInstance().getInstalledPackages(activity.getPackageManager());
        NavigationDrawerFragment.setDrawerIndicatorEnabled(true);
        FlurryWrapper.logEvent(TrackingEvents.UserViewedSelectApp);
        setFragmentTag(Constants.TAG_SELECT_APP_FRAGMENT);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        loader.progressiveStop();
        loader.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_app, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        ActionBarUtils.getInstance().hideActionBarShadow();

        gridView.setOnItemClickListener(this);
        filterApps();

    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_out_top);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().showActionBarShadow();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public int getTitle() {
        return R.string.title_select_app;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(userAppsAdapter == null) {
            return;
        }
        NavUtils.getInstance(activity).startSaveAppFragment(userAppsAdapter.getItem(position));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_select_app, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search2));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                FlurryWrapper.logEvent(TrackingEvents.UserSearchedAppToAdd, new HashMap<String, String>(){{
                    put("query", s);
                }});
                reset();
                appInfos = new ArrayList<>();
                List<ApplicationInfo> apps = PackagesUtils.getInstance().getInstalledPackages(activity.getPackageManager());
                for (ApplicationInfo app : apps) {
                    String appName = (String) activity.getPackageManager().getApplicationLabel(app);
                    if (appName.toLowerCase().contains(s.toLowerCase())) {
                        appInfos.add(app);
                    }
                }

                filterApps();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search2), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (item.getItemId() == R.id.action_search2) {
                    reset();
                    appInfos = PackagesUtils.getInstance().getInstalledPackages(activity.getPackageManager());
                    filterApps();
                }
                return true;
            }
        });
    }

    private void reset() {
        userAppsAdapter = null;
        endlessScrollListener.resetPreviousTotal();
        currentPage = 0;
        totalPages = -1;
    }

    @Subscribe
    public void onAppSubmitted(AppSubmittedEvent event) {
        userAppsAdapter.removeApp(event.getPackageName());
        if(userAppsAdapter.getCount() == 0) {
            appInfos = PackagesUtils.getInstance().getInstalledPackages(activity.getPackageManager());
            reset();
            filterApps();
        }
    }

    private void displayEmptyAppsView() {
        info.setText("There are no apps you can share with AppHunt at the moment!");
        gridView.setVisibility(View.GONE);
        noAppsView.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        gridView.setVisibility(View.VISIBLE);
        info.setText(R.string.info_select_app);
        noAppsView.setVisibility(View.GONE);
    }

    @Subscribe
    public void onFilteredPackagesReceived(PackagesFilteredApiEvent event) {
        if(event.getPackages() != null) {
            boolean isUserAdapterEmpty = userAppsAdapter == null || userAppsAdapter.getCount() == 0;
            boolean arePackagesAvailable = event.getPackages().getAvailablePackages() != null &&
                    event.getPackages().getAvailablePackages().size() > 0;

            if(!arePackagesAvailable && currentPage != totalPages) {
                filterApps();
                return;
            }

            if(currentPage == totalPages && isUserAdapterEmpty && !arePackagesAvailable) {
                loader.progressiveStop();
                displayEmptyAppsView();
                return;
            }
            else {
                hideEmptyView();
            }

            List<ApplicationInfo> tempData = new ArrayList<>();

            for (ApplicationInfo info : appInfos) {
                for (String packageName : event.getPackages().getAvailablePackages()) {
                    if (info.packageName.equals(packageName)) {
                        tempData.add(info);
                    }
                }
            }

            if(userAppsAdapter == null) {
                userAppsAdapter = new InstalledAppsAdapter(activity, tempData);
                endlessScrollListener.resetPreviousTotal();
                gridView.setAdapter(userAppsAdapter);
                gridView.setOnScrollListener(endlessScrollListener);
                loader.progressiveStop();
            } else {
                userAppsAdapter.addAll(tempData);
            }
        }
    }

    private void filterApps() {
        if(appInfos.size() == 0) {
            displayEmptyAppsView();
            return;
        }

        if(currentPage == totalPages) {
            return;
        }

        currentPage++;
        if(totalPages == -1) {
            if(appInfos.size() % PAGE_SIZE > 0) {
                totalPages = (appInfos.size() / PAGE_SIZE) + 1;
            } else {
                totalPages = (appInfos.size() / PAGE_SIZE);
            }
        }
        new LoadInstalledApps().execute();
    }

    private class LoadInstalledApps extends AsyncTask<Void, Void, Packages> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Packages doInBackground(Void... params) {
            Packages packages = new Packages();

            int totalSize = currentPage * PAGE_SIZE;
            if(totalSize > appInfos.size()) {
                totalSize = appInfos.size();
                currentPage = totalPages;
            }

            final int startPoint = (currentPage - 1) * PAGE_SIZE;
            for (int i = startPoint; i < totalSize; i++) {
                packages.getPackages().add(appInfos.get(i).packageName);
            }

            return packages;
        }

        @Override
        protected void onPostExecute(Packages packages) {
            super.onPostExecute(packages);
            ApiClient.getClient(getActivity()).filterApps(packages);
        }
    }
}
