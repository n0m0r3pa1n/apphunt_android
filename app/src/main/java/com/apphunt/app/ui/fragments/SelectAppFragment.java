package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
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
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.listeners.EndlessScrollListener;
import com.apphunt.app.utils.PackagesUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class SelectAppFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectAppFragment.class.getName();
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private int totalPages = -1;

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
    private List<ApplicationInfo> data;
    private ActionBarActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlurryAgent.logEvent(TrackingEvents.UserViewedSelectApp);
        setFragmentTag(Constants.TAG_SELECT_APP_FRAGMENT);
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
        gridView.setOnScrollListener(new EndlessScrollListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                filterApps();
            }
        }));
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActionBarActivity) activity;
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
        NavUtils.getInstance(activity).startSaveAppFragment(userAppsAdapter.getItem(position));
    }

    @Subscribe
    public void onAppSubmitted(AppSubmittedEvent event) {
        userAppsAdapter.removeApp(event.getPackageName());
        if(userAppsAdapter.getCount() == 0) {
            displayEmptyAppsView();
        }
    }

    private void displayEmptyAppsView() {
        info.setText("There are no apps you can share with AppHunt at the moment!");
        noAppsView.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onFilteredPackagesReceived(PackagesFilteredApiEvent event) {
       if(event.getPackages() != null) {
           List<ApplicationInfo> tempData = new ArrayList<>();

           for (ApplicationInfo info : data) {
               for (String packageName : event.getPackages().getAvailablePackages()) {
                   if (info.packageName.equals(packageName)) {
                       tempData.add(info);
                   }
               }
           }

           if(userAppsAdapter == null) {
               if(event.getPackages().getAvailablePackages() == null ||
                       event.getPackages().getAvailablePackages().size() == 0) {
                   loader.progressiveStop();
                   displayEmptyAppsView();
                   return;
               }

               userAppsAdapter = new InstalledAppsAdapter(activity, tempData);
               Handler delayHandler = new Handler();
               delayHandler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       loader.progressiveStop();
                       gridView.setAdapter(userAppsAdapter);
                   }
               }, 250);
           } else {
               userAppsAdapter.addAll(tempData);
           }
       }
    }

    private void filterApps() {
        if(currentPage == totalPages) {
            return;
        }

        currentPage++;
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
            data = PackagesUtils.getInstance().getInstalledPackages(activity.getPackageManager());
            if(totalPages == -1) {
                if(data.size() % PAGE_SIZE > 0) {
                    totalPages = (data.size() / PAGE_SIZE) + 1;
                } else {
                    totalPages = (data.size() / PAGE_SIZE);
                }
            }

            int totalSize = currentPage * PAGE_SIZE;
            if(totalSize > data.size()) {
                totalSize = data.size();
                currentPage = totalPages;
            }

            final int startPoint = (currentPage - 1) * PAGE_SIZE;
            for (int i = startPoint; i < totalSize; i++) {
                packages.getPackages().add(data.get(i).packageName);
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
