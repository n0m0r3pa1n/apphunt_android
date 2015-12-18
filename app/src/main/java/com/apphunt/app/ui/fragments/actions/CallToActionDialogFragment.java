package com.apphunt.app.ui.fragments.actions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.LoginType;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.LoginUtils;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nmp on 15-12-18.
 */
public class CallToActionDialogFragment extends DialogFragment {

    private Activity acivity;
    private String title, description, actionName;
    private int imageRes;
    private String loginType;

    @InjectView(R.id.title)
    TextView titleView;

    @InjectView(R.id.image)
    CircleImageView imageView;

    @InjectView(R.id.desc)
    TextView descriptionView;

    @InjectView(R.id.action_button)
    TextView actionButton;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_call_to_action, null);
        ButterKnife.inject(this, view);
        builder.setView(view);

        loginType = LoginProviderFactory.get(acivity).getLoginType();
        FlurryWrapper.logEvent(TrackingEvents.UserViewedCallToAction, new HashMap<String, String>(){{
            put("loginType", loginType);
        }});
        if (loginType.equals(LoginType.Facebook.toString())) {
            title = "We're on Facebook!";
            description = "Hey hunter, do you know that we have a Facebook page? Like us and stay tuned with AppHunt!";
            actionName = "Like Our Page";
            imageRes = R.drawable.ic_about_facebook;
        } else if (loginType.equals(LoginType.Twitter.toString())) {
            title = "We're on Twitter!";
            description = "Hey hunter, do you know that we have a Twitter page? Follow us and stay tuned with AppHunt!";
            actionName = "Follow Us";
            imageRes = R.drawable.ic_about_twitter;
        } else if (loginType.equals(LoginType.GooglePlus.toString())) {
            title = "We're on G+!";
            description = "Hey hunter, do you know that we have a Google+ community? Join us and stay tuned with AppHunt!";
            actionName = "Join Now";
            imageRes = R.drawable.ic_about_google;
        } else {
            title = "Time to Login!";
            description = "With login you can favourite apps, become a Top hunter, create your own collection of apps, make connections with " +
                    "other users and much more!";
            actionName = "Login Now";
            imageRes = R.drawable.ic_contact_picture;
        }

        titleView.setText(title);
        descriptionView.setText(description);
        actionButton.setText(actionName);
        imageView.setImageResource(imageRes);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.acivity = activity;
    }

    @OnClick(R.id.action_button)
    public void onActionClick() {
        if (loginType.equals(LoginType.Facebook.toString())) {
            Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/theapphunt"));
            startActivity(share);
        } else if (loginType.equals(LoginType.Twitter.toString())) {
            Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/TheAppHunt"));
            startActivity(share);
        } else if (loginType.equals(LoginType.GooglePlus.toString())) {
            Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/105538441962130139197"));
            startActivity(share);
        } else {
            LoginUtils.showLoginFragment(false);
        }

        FlurryWrapper.logEvent(TrackingEvents.UserClickedCallToAction, new HashMap<String, String>(){{
            put("loginType", loginType);
        }});
        this.dismiss();
    }
}
