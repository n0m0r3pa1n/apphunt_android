package com.shtaigaway.apphunt.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;

import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.ui.adapters.UserAppsAdapter;
import com.shtaigaway.apphunt.ui.interfaces.OnAppSelectedListener;
import com.shtaigaway.apphunt.utils.InstalledPackagesUtils;

public class SelectAppFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private View view;
    private UserAppsAdapter userAppsAdapter;

    private OnAppSelectedListener callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.select_app);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_app, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        GridView gridView = (GridView) view.findViewById(R.id.gv_apps_list);
        gridView.setOnItemClickListener(this);

        userAppsAdapter = new UserAppsAdapter(getActivity(), InstalledPackagesUtils.installedPackages(getActivity().getPackageManager()));
        gridView.setAdapter(userAppsAdapter);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_top);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (OnAppSelectedListener) activity;
        } catch (ClassCastException e) {
            Log.e("TAG", e.getMessage());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callback.onAppSelected(userAppsAdapter.getItem(position));
    }
}
