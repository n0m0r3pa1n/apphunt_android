package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.AppHuntApiClient;
import com.apphunt.app.api.apphunt.callback.Callback;
import com.apphunt.app.api.apphunt.models.SaveApp;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.HideFragmentEvent;
import com.apphunt.app.event_bus.events.LoginSkippedEvent;
import com.apphunt.app.event_bus.events.ShowNotificationEvent;
import com.apphunt.app.event_bus.events.UserCreatedEvent;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.NotificationsUtils;
import com.apphunt.app.utils.StatusCode;
import com.apphunt.app.utils.TrackingEvents;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SaveAppFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = SaveAppFragment.class.getName();

    private View view;
    private EditText desc;
    private ApplicationInfo data;
    private ActionBarActivity activity;

    @InjectView(R.id.save)
    Button saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_save_app);
        data = getArguments().getParcelable(Constants.KEY_DATA);
        Map<String, String> params = new HashMap<>();
        params.put("appPackage", data.packageName);
        FlurryAgent.logEvent(TrackingEvents.UserViewedAddApp, params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_save_app, container, false);
        ButterKnife.inject(this, view);
        initUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void initUI() {
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);
        container.setOnClickListener(this);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(data.loadLabel(activity.getPackageManager()));

        ImageView icon = (ImageView) view.findViewById(R.id.app_icon);
        icon.setImageDrawable(data.loadIcon(activity.getPackageManager()));

        desc = (EditText) view.findViewById(R.id.description);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.container:
                closeKeyboard(desc);
                break;
        }
    }

    @OnClick(R.id.save)
    public void saveApp() {
        if(!LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            showLoginFragment();
            return;
        }

        if (desc.getText() != null && desc.getText().length() >= 50) {
            showLoginFragment();
        } else if (desc.getText() != null && desc.getText().length() > 0 && desc.getText().length() <= 50) {
            desc.setHint(R.string.hint_short_description);
            desc.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
            desc.setError("Min 50 chars");
            vibrate();
        } else if (desc.getText() == null || desc.getText() != null && desc.getText().length() == 0) {
            desc.setHint(R.string.hint_please_enter_description);
            desc.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
            vibrate();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }

    private void showLoginFragment() {
        Random random = new Random();
        int currPercent = random.nextInt(100) + 1;
        if(currPercent <= Constants.USER_SKIP_LOGIN_PERCENTAGE) {
            FlurryAgent.logEvent(TrackingEvents.AppShowedSkippableLogin);
            LoginUtils.showSkippableLoginFragment(getActivity());
        } else {
            FlurryAgent.logEvent(TrackingEvents.AppShowedRegularLogin);
            LoginUtils.showLoginFragment(getActivity());
        }
    }

    private void saveApp(final View v, String userId) {
        v.setEnabled(false);
        SaveApp app = new SaveApp();
        app.setDescription(desc.getText().toString());
        app.setPackageName(data.packageName);
        app.setPlatform(Constants.PLATFORM);
        app.setUserId(userId);

        AppHuntApiClient.getClient().saveApp(app, new Callback() {
            @Override
            public void success(Object o, Response response) {
                int statusCode = response.getStatus();
                if(!isAdded()) {
                    return;
                }
                if (statusCode == StatusCode.SUCCESS.getCode()) {
                    FlurryAgent.logEvent(TrackingEvents.UserAddedApp);
                    BusProvider.getInstance().post(new HideFragmentEvent(Constants.TAG_SAVE_APP_FRAGMENT));
                    BusProvider.getInstance().post(new ShowNotificationEvent(getString(R.string.saved_successfully)));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (!isAdded()) {
                    return;
                }
                try {
                    activity.getSupportFragmentManager().popBackStack();
                    String message = getString(R.string.not_available_in_the_store);
                    if(error.getResponse().getStatus() != 404) {
                        message = getString(R.string.server_error);
                    }
                    NotificationsUtils.showNotificationFragment(activity, message, false, false);
                    v.setEnabled(true);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                FlurryAgent.logEvent(TrackingEvents.UserAddedUnknownApp);
            }
        });
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (ActionBarActivity) activity;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        closeKeyboard(desc);
    }

    @Subscribe
    public void onLoginSkipped(LoginSkippedEvent event) {
        FlurryAgent.logEvent(TrackingEvents.UserSkippedLoginWhenAddApp);
        saveApp(saveButton, Constants.APPHUNT_ADMIN_USER_ID);
    }

    @Subscribe
    public void onUserCreated(UserCreatedEvent event) {
        saveApp(saveButton, event.getUser().getId());
    }

}
