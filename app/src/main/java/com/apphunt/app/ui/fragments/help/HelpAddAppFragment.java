package com.apphunt.app.ui.fragments.help;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.utils.ui.ActionBarUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-9.
 */
public class HelpAddAppFragment extends BaseFragment {
    private Activity activity;

    public HelpAddAppFragment() {
        setTitle(R.string.title_help);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
        ActionBarUtils.getInstance().setTitle(R.string.title_help_add_app);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_add_app, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().setPreviousTitle();
    }
}
