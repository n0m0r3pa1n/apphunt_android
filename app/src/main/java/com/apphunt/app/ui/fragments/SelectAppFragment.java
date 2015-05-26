package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.Packages;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.PackagesFilteredApiEvent;
import com.apphunt.app.ui.adapters.UserAppsAdapter;
import com.apphunt.app.utils.InstalledPackagesUtils;
import com.apphunt.app.utils.TrackingEvents;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SelectAppFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = SelectAppFragment.class.getName();

    private View view;
    private GridView gridView;
    private UserAppsAdapter userAppsAdapter;

    private List<ApplicationInfo> data;
    private ActionBarActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_select_app);
        FlurryAgent.logEvent(TrackingEvents.UserViewedSelectApp);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_app, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        LoadersUtils.showCenterLoader(activity, R.drawable.loader_white);

        gridView = (GridView) view.findViewById(R.id.gv_apps_list);
        gridView.setOnItemClickListener(this);

        new LoadInstalledApps().execute();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LoadersUtils.hideCenterLoader(activity);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NavUtils.getInstance(activity).startSaveAppFragment(userAppsAdapter.getItem(position));
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

           LoadersUtils.hideCenterLoader(activity);
           userAppsAdapter = new UserAppsAdapter(activity, tempData);
           gridView.setAdapter(userAppsAdapter);
       }
    }

    private class LoadInstalledApps extends AsyncTask<Void, Void, Packages> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Packages doInBackground(Void... params) {
            Packages packages = new Packages();
            data = InstalledPackagesUtils.installedPackages(activity.getPackageManager());
            for (ApplicationInfo info : data) {
                packages.getPackages().add(info.packageName);
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
