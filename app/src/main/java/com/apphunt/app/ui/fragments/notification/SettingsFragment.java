package com.apphunt.app.ui.fragments.notification;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.services.InstallService;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.constants.TrackingEvents;
import com.flurry.android.FlurryAgent;

public class SettingsFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = SettingsFragment.class.getName();

    private View view;
    private RelativeLayout settingsLayout;
    private ToggleButton notifications;
    private ToggleButton sounds;
    private ToggleButton installNotification;
    private ActionBarActivity activity;
    private Button dismissBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.getInstance().hideActionBarShadow();
        FlurryAgent.logEvent(TrackingEvents.UserViewedSettings);

        setFragmentTag(Constants.TAG_SETTINGS_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        initUI();

        return view;

    }

    private void initUI() {
        settingsLayout = (RelativeLayout) view.findViewById(R.id.settings);

        notifications = (ToggleButton) view.findViewById(R.id.notification_toggle);
        notifications.setOnCheckedChangeListener(this);
        notifications.setChecked(SharedPreferencesHelper.getBooleanPreference(Constants.SETTING_NOTIFICATIONS_ENABLED, true));

        sounds = (ToggleButton) view.findViewById(R.id.sounds_toggle);
        sounds.setOnCheckedChangeListener(this);
        sounds.setChecked(SharedPreferencesHelper.getBooleanPreference(Constants.IS_SOUNDS_ENABLED, true));

        installNotification = (ToggleButton) view.findViewById(R.id.installed_apps_toggle);
        installNotification.setOnCheckedChangeListener(this);
        installNotification.setChecked(SharedPreferencesHelper.getBooleanPreference(Constants.IS_INSTALL_NOTIFICATION_ENABLED,
                true));

        dismissBtn = (Button) view.findViewById(R.id.dismiss);
        dismissBtn.setOnClickListener(this);
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
                    settingsLayout.startAnimation(notificationEnterAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            return enterAnim;
        } else {
            Animation outAnim = AnimationUtils.loadAnimation(activity, R.anim.alpha_out);
            settingsLayout.startAnimation(AnimationUtils.loadAnimation(activity,
                    R.anim.slide_out_top));

            return outAnim;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.notification_toggle:
                if (isChecked) {
                    NotificationsUtils.setupDailyNotificationService(activity);
                } else {
                    NotificationsUtils.disableDailyNotificationsService(activity);
                }
                break;

            case R.id.sounds_toggle:
                if (!isChecked) {
                    FlurryAgent.logEvent(TrackingEvents.UserDisabledSound);
                }

                SharedPreferencesHelper.setPreference(Constants.IS_SOUNDS_ENABLED, isChecked);
                break;
            case R.id.installed_apps_toggle:
                if (!isChecked) {
                    FlurryAgent.logEvent(TrackingEvents.UserDisabledInstalledAppsNotification);
                }
                SharedPreferencesHelper.setPreference(Constants.IS_INSTALL_NOTIFICATION_ENABLED, isChecked);
                InstallService.setupService(getActivity());
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (ActionBarActivity) activity;
    }

    @Override
    public int getTitle() {
        return R.string.title_settings;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().showActionBarShadow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dismiss:
                activity.getSupportFragmentManager().popBackStack();
                break;
        }
    }
}
