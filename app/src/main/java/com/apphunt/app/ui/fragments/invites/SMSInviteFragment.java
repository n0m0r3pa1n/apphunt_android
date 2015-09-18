package com.apphunt.app.ui.fragments.invites;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.utils.DeepLinkingUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/24/15.
 * *
 * * NaughtySpirit 2015
 */
public class SMSInviteFragment extends BaseFragment {
    private static final String TAG = SMSInviteFragment.class.getSimpleName();

    private AppCompatActivity activity;

    @InjectView(R.id.phone_number)
    EditText phoneNumber;

    public static SMSInviteFragment newInstance() {
        SMSInviteFragment fragment = new SMSInviteFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent(TrackingEvents.UserViewedSMSInvitation);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_sms, container, false);
        ButterKnife.inject(this, view);

        phoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendSMS();
                    handled = true;
                }

                return handled;
            }
        });

        phoneNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                phoneNumber.setText("+");
                return false;
            }
        });

        return view;
    }

    @OnClick(R.id.send)
    public void onSendEmailClick() {
        sendSMS();
    }

    private void sendSMS(){
        if (phoneNumber.getText() == null || phoneNumber.getText().length() == 0) {
            phoneNumber.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
            phoneNumber.setError("Empty field");
        } else {
            User user = LoginProviderFactory.get(activity).getUser();

            ArrayList<DeepLinkingUtils.DeepLinkingParam> params = new ArrayList<>();
            params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_DL_TYPE, "welcome"));
            params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_ID, user.getId()));
            params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_NAME, user.getName()));
            params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_PROFILE_IMAGE_URL, user.getProfilePicture()));

            closeKeyboard(phoneNumber);

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber.getText().toString(), null,
                    String.format(getString(R.string.sms_invite_body), user.getName(),
                            DeepLinkingUtils.getInstance(activity).generateShortUrl(params)), null, null);

            activity.getSupportFragmentManager().popBackStack();
            NotificationsUtils.showNotificationFragment((ActionBarActivity) activity, getString(R.string.msg_successful_invite), false, false);
            FlurryAgent.logEvent(TrackingEvents.UserSentSMSInvite);
        }
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        closeKeyboard(phoneNumber);
    }
}