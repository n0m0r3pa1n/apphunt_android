package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.smart_rate.SmartRate;
import com.apphunt.app.ui.interfaces.OnNetworkStateChange;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.apphunt.app.utils.SoundsUtils;

public class NotificationFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = NotificationFragment.class.getName();

    private String notification;
    private boolean showSettingsBtn = false;

    private View view;
    private RelativeLayout notificationLayout;
    private TextView notificationText;
    private Button dismissBtn;
    private Button settingsBtn;

    private OnNetworkStateChange networkStateCallback;
    private ActionBarActivity activity;
    private boolean showRating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.getInstance().hideAbShadow();
        setTitle(R.string.title_notification);
        notification = getArguments().getString(Constants.KEY_NOTIFICATION);
        showSettingsBtn = getArguments().getBoolean(Constants.KEY_SHOW_SETTINGS);
        showRating = getArguments().getBoolean(Constants.KEY_SHOW_RATING);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        notificationLayout = (RelativeLayout) view.findViewById(R.id.notification);

        notificationText = (TextView) view.findViewById(R.id.notification_text);
        notificationText.setText(notification);

        dismissBtn = (Button) view.findViewById(R.id.dismiss);
        dismissBtn.setOnClickListener(this);

        if (showSettingsBtn) {
            settingsBtn = (Button) view.findViewById(R.id.open_settings);
            settingsBtn.setVisibility(View.VISIBLE);
            settingsBtn.setOnClickListener(this);
        }
    }

    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation enterAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_in);
            enterAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    SoundsUtils.playSound(activity, R.raw.notification_2);
                    Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(300);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation notificationEnterAnim = AnimationUtils.loadAnimation(activity,
                            R.anim.slide_in_top_notification);
                    notificationEnterAnim.setFillAfter(true);
                    notificationLayout.startAnimation(notificationEnterAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            return enterAnim;
        } else {
            Animation outAnim = AnimationUtils.loadAnimation(activity, R.anim.alpha_out);
            notificationLayout.startAnimation(AnimationUtils.loadAnimation(activity,
                    R.anim.slide_out_top));

            return outAnim;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dismiss:
//                if(showSettingsBtn) {
//                    while (activity.getSupportFragmentManager().getBackStackEntryCount() > 0){
//                        activity.getSupportFragmentManager().popBackStackImmediate();
//                    }
//                } else

                if (showRating) {
                    activity.getSupportFragmentManager().popBackStack();
                    SmartRate.show(Constants.SMART_RATE_LOCATION_APP_SAVED);
                }

                activity.getSupportFragmentManager().popBackStack();
                LoadersUtils.hideCenterLoader(activity);
                break;

            case R.id.open_settings:
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), Constants.REQUEST_NETWORK_SETTINGS);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3 && ConnectivityUtils.isNetworkAvailable(activity)) {
            networkStateCallback.onNetworkAvailable();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (ActionBarActivity) activity;

        try {
            networkStateCallback = (OnNetworkStateChange) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LoadersUtils.hideCenterLoader(activity);
        ActionBarUtils.getInstance().showAbShadow();
    }
}
