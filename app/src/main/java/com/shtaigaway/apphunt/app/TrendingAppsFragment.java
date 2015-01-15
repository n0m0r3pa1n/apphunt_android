package com.shtaigaway.apphunt.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.shtaigaway.apphunt.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Naughty Spirit
 * on 1/15/15.
 */
public class TrendingAppsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trending_apps_layout, container, false);
        ListView listView = (ListView)rootView.findViewById(R.id.trending_app_list);

        List<App> appList = new ArrayList<>(Arrays.asList(new App("App1"), new App("App2")));
        listView.setAdapter(new AppAdapter(getActivity(), appList));
        return rootView;
    }
}
