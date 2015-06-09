package com.apphunt.app.ui.fragments.help;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.utils.ui.ActionBarUtils;

import butterknife.ButterKnife;

/**
 * Created by nmp on 15-6-9.
 */
public class HelpFragment extends BaseFragment {
    private Activity activity;

    public HelpFragment() {
        setTitle(R.string.title_help);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.inject(this, view);

        RelativeLayout addAppItem = (RelativeLayout) view.findViewById(R.id.add_app_item);
        RelativeLayout requirementsItem = (RelativeLayout) view.findViewById(R.id.requirements_item);
        RelativeLayout topHuntersItem = (RelativeLayout) view.findViewById(R.id.top_hunters_item);

        addAppItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActionBarActivity)activity).getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new HelpAddAppFragment(), "TAgaaa").commit();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
        ActionBarUtils.getInstance().setTitle(R.string.title_help);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().setPreviousTitle();
    }
}
