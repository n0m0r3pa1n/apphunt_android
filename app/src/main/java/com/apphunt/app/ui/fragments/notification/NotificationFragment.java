package com.apphunt.app.ui.fragments.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.smart_rate.SmartRate;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnActionNeeded;
import com.apphunt.app.utils.SoundsUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.LoadersUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NotificationFragment extends BaseFragment {

    private static final String TAG = NotificationFragment.class.getName();

    private View view;

    @InjectView(R.id.notification)
    RelativeLayout notificationLayout;

    @InjectView(R.id.notification_text)
    TextView notificationText;

    @InjectView(R.id.dismiss)
    Button dismissBtn;

    @InjectView(R.id.open_settings)
    Button settingsBtn;

    @InjectView(R.id.show_continue)
    Button continueBtn;

    private String notification;
    private boolean showSettingsBtn = false;

    private ActionBarActivity activity;
    private boolean showRating;
    private boolean showShadow;
    private boolean showContinue;

    private OnActionNeeded actionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.getInstance().hideActionBarShadow();

        notification = getArguments().getString(Constants.KEY_NOTIFICATION);
        showSettingsBtn = getArguments().getBoolean(Constants.KEY_SHOW_SETTINGS);
        showRating = getArguments().getBoolean(Constants.KEY_SHOW_RATING);
        showShadow = getArguments().getBoolean(Constants.KEY_SHOW_SHADOW);
        showContinue = getArguments().getBoolean(Constants.KEY_SHOW_CONTINUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    @Override
    public int getTitle() {
        return R.string.title_notification;
    }

    private void initUI() {
        notificationText.setText(notification);

        if (showSettingsBtn) {
            settingsBtn.setVisibility(View.VISIBLE);
        }

        if (showContinue) {
            continueBtn.setVisibility(View.VISIBLE);
        }
    }

    public void setActionListener(OnActionNeeded actionListener) {
        this.actionListener = actionListener;
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

    @OnClick(R.id.dismiss)
    public void onDismissClick() {
        if (showRating) {
            activity.getSupportFragmentManager().popBackStack();
            SmartRate.show(Constants.SMART_RATE_LOCATION_APP_SAVED);
        }

        activity.getSupportFragmentManager().popBackStack();
        LoadersUtils.hideCenterLoader(activity);
    }

    @OnClick(R.id.open_settings)
    public void onSettingsClick() {
        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), Constants.REQUEST_NETWORK_SETTINGS);
    }

    @OnClick(R.id.show_continue)
    public void onContinueClick() {
        actionListener.onContinueAction();
        activity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (ActionBarActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LoadersUtils.hideCenterLoader(activity);

        if (showShadow) {
            ActionBarUtils.getInstance().showActionBarShadow();
        } else {
            ActionBarUtils.getInstance().hideActionBarShadow();
        }
    }
}
