package com.shtaigaway.apphunt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Naughty Spirit
 * on 1/15/15.
 */
public class TrendingAppsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trending_apps_layout, container, false);
        ListView listView = (ListView)rootView.findViewById(R.id.trending_app_list);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[] {"App 1", "App 2"}));
        return rootView;
    }
}
