package com.apphunt.app.ui.fragments.notification;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.navigation.NavigationDrawerFragment;
import com.apphunt.app.ui.interfaces.OnActionNeeded;
import com.apphunt.app.utils.PackagesUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by nmp on 15-7-30.
 */
public class UpdateRequiredFragment extends DialogFragment {

    private static final String TAG = NotificationFragment.class.getName();

    private ActionBarActivity activity;

    public static UpdateRequiredFragment newInstance() {
        return new UpdateRequiredFragment();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_update_required, null);
        ButterKnife.inject(this, view);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActionBarActivity) activity;
    }

    @OnClick(R.id.update)
    public void update() {
        PackagesUtils.openInMarket(activity, "com.apphunt.app");
    }
}
