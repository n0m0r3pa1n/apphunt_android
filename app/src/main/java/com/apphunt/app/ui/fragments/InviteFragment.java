package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.utils.Constants;
import com.facebook.widget.FacebookDialog;

public class InviteFragment extends BaseFragment implements View.OnClickListener{
    private Activity activity;
    private Animation alphaOut, alphaIn;

    private static final int ANIM_LONG_DURATION = 1400;
    private static final int ANIM_SHORT_DURATION = 500;

    private ImageView appHuntLogo;
    private TextView share, getInvite;

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

        initUI(view);
        initAnimations();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appHuntLogo.startAnimation(alphaIn);
            }
        }, 400);

        return view;
    }

    private void initUI(View view) {
        appHuntLogo = (ImageView) view.findViewById(R.id.apphunt_background);
        share = (TextView) view.findViewById(R.id.share);
        getInvite = (TextView) view.findViewById(R.id.get_invite);
    }

    private void initAnimations() {
        alphaIn = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_in);
        alphaIn.setDuration(ANIM_LONG_DURATION);
        alphaOut = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_out);
        alphaOut.setDuration(ANIM_LONG_DURATION);

        alphaIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appHuntLogo.setVisibility(View.VISIBLE);
                appHuntLogo.startAnimation(alphaOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        alphaOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appHuntLogo.setVisibility(View.GONE);
                showText();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showText() {
        clearAnimations();
        alphaIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getInvite.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        getInvite.startAnimation(alphaIn);
        share.startAnimation(alphaIn);
    }

    private void clearAnimations() {
        alphaIn.setAnimationListener(null);
        alphaOut.setAnimationListener(null);
        alphaIn.setDuration(ANIM_SHORT_DURATION);
        alphaOut.setDuration(ANIM_SHORT_DURATION);
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
                .setPicture(Constants.LAUNCHROCK_ICON)
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
