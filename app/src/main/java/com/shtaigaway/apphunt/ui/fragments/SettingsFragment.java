package com.shtaigaway.apphunt.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.NotificationsUtils;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;

public class SettingsFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private View view;
    private RelativeLayout settingsLayout;
    private ToggleButton notifications;
    private Activity activity;
    private Button dismissBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings);
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
        notifications.setChecked(SharedPreferencesHelper.getBooleanPreference(activity, Constants.IS_DAILY_NOTIFICATION_ENABLED));
        dismissBtn = (Button) view.findViewById(R.id.dismiss);
        dismissBtn.setOnClickListener(this);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation enterAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_in);
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
            Animation outAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_out);;

            settingsLayout.startAnimation(AnimationUtils.loadAnimation(activity,
                    R.anim.slide_out_top));

            return outAnim;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            NotificationsUtils.setupDailyNotificationService(getActivity());
        } else {
            NotificationsUtils.disableDailyNotificationsService(getActivity());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dismiss:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }
}
