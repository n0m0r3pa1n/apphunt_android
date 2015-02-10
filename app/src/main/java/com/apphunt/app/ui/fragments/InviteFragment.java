package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.utils.Constants;
import com.facebook.widget.FacebookDialog;

public class InviteFragment extends BaseFragment implements View.OnClickListener{
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_invite);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        TextView share = (TextView) view.findViewById(R.id.share);
        share.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                shareWithMessenger();
                break;
        }
    }

    private void shareWithMessenger() {
        FacebookDialog.MessageDialogBuilder builder = new FacebookDialog.MessageDialogBuilder(getActivity())
                .setLink(Constants.GOOGLE_PLAY_APP_URL)
                .setName("AppHunt")
                .setCaption("Build great social apps that engage your friends.")
                .setPicture("https://launchrock-assets.s3.amazonaws.com/logo-files/LWPRHM35_1421410706452.png?_=4")
                .setDescription("Allow your users to message links from your app using the Android SDK.")
                .setFragment(this);

        // If the Facebook app is installed and we can present the share dialog
        if (builder.canPresent()) {
            FacebookDialog dialog = builder.build();
            dialog.present();
            // Enable button or other UI to initiate launch of the Message Dialog
        }  else {
            Log.d("INVITE", "FAILED");
            share();
            // Disable button or other UI for Message Dialog
        }
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(android.content.Intent.EXTRA_TITLE, "Om nom nom");
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Om nom nom");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");

        startActivity(Intent.createChooser(sendIntent, "Om nom nom"));
    }
}
