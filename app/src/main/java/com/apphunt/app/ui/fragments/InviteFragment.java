package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apphunt.app.R;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.facebook.widget.FacebookDialog;

import it.appspice.android.AppSpice;

public class InviteFragment extends BaseFragment implements View.OnClickListener{
    private static final String TAG = InviteFragment.class.getSimpleName();
    private static final int REQUEST_CODE_SHARE_INTENT = 5;

    private ActionBarActivity activity;
    private RelativeLayout inviteLayout;

    private Button shareBtn;
    private TextView inviteText;

    private int sharesLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity.setTitle(getString(R.string.title_invite));
        
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        inviteLayout = (RelativeLayout) view.findViewById(R.id.invite);
        shareBtn = (Button) view.findViewById(R.id.share);
        shareBtn.setOnClickListener(this);

        sharesLeft = SharedPreferencesHelper.getIntPreference(activity, Constants.KEY_INVITE_SHARE, Constants.INVITE_SHARES_COUNT);
        inviteText = (TextView) view.findViewById(R.id.invite_text);
        inviteText.setText(String.format(getString(R.string.invite_text), sharesLeft));

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActionBarActivity)activity;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.share:
                share();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK || requestCode == REQUEST_CODE_SHARE_INTENT) {
            updateSharesLeft();
            if(sharesLeft <= 0) {
                AppSpice.createEvent(TrackingEvents.UserReceivedInvite).track();
                activity.getSupportFragmentManager().beginTransaction().hide(this).commit();
                activity.setTitle(getString(R.string.app_name));
                Toast.makeText(activity, "Sweet! You are now an app hunter :)", Toast.LENGTH_LONG).show();
            } else {
                if(sharesLeft == 1) {
                    inviteText.setText(String.format(getString(R.string.invite_one_text), sharesLeft));
                } else {
                    inviteText.setText(String.format(getString(R.string.invite_text), sharesLeft));
                }
            }
        }
    }

    private void updateSharesLeft() {
        sharesLeft--;
        SharedPreferencesHelper.setPreference(activity, Constants.KEY_INVITE_SHARE, sharesLeft);
        AppSpice.createEvent(TrackingEvents.UserShareForInviteCountDecremented).track();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation enterAnim = AnimationUtils.loadAnimation(activity, R.anim.alpha_in);
            enterAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation notificationEnterAnim = AnimationUtils.loadAnimation(activity,
                            R.anim.slide_in_top_notification);
                    notificationEnterAnim.setFillAfter(true);
                    inviteLayout.startAnimation(notificationEnterAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            return enterAnim;
        } else {
            Animation outAnim = AnimationUtils.loadAnimation(activity, R.anim.alpha_out);
            inviteLayout.startAnimation(AnimationUtils.loadAnimation(activity,
                    R.anim.slide_out_top));

            return outAnim;
        }
    }

    private void share() {
        AppSpice.createEvent(TrackingEvents.UserSharedForInvite).track();
        FacebookDialog.MessageDialogBuilder builder = new FacebookDialog.MessageDialogBuilder(getActivity())
                .setLink(Constants.BIT_LY_GOOGLE_PLAY_URL)
                .setName("AppHunt")
                .setCaption("Your personal invite for AppHunt")
                .setPicture(Constants.LAUNCHROCK_ICON)
                .setDescription("Find the best new apps, every day!")
                .setFragment(this);

        if (builder.canPresent()) {
            FacebookDialog dialog = builder.build();
            dialog.present();
        }  else {
            shareWithIntent();
       }
    }

    private void shareWithIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject_text));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "AppHunt - find the best new apps, every day! \n" + Constants.BIT_LY_GOOGLE_PLAY_URL);
        startActivityForResult(Intent.createChooser(sendIntent, "Share AppHunt"), REQUEST_CODE_SHARE_INTENT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}
