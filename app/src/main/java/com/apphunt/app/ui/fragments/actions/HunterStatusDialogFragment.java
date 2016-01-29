package com.apphunt.app.ui.fragments.actions;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.ads.UserAdStatus;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.FlurryWrapper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nmp on 16-1-29.
 */
public class HunterStatusDialogFragment extends DialogFragment {
    public static final String TAG = HunterStatusDialogFragment.class.getSimpleName();
    @InjectView(R.id.title)
    TextView titleView;

    @InjectView(R.id.image)
    CircleImageView imageView;

    @InjectView(R.id.desc)
    TextView descriptionView;

    @InjectView(R.id.action_button)
    TextView actionButton;

    private UserAdStatus userAdStatus;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FlurryWrapper.logEvent(TrackingEvents.UserViewedHunterStatusDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_call_to_action, null);
        ButterKnife.inject(this, view);
        builder.setView(view);

        imageView.setImageResource(R.drawable.ic_trophy);
        titleView.setText("Your Hunter Status");
        actionButton.setText("Let's Hunt!");
        descriptionView.setText("The chat feature is only available for users who have been Top Hunters! " +
                "Become one and you can join our awesome discussions!");

        return builder.create();
    }

    @OnClick(R.id.action_button)
    public void onActionClick() {
        this.dismiss();
    }
}


