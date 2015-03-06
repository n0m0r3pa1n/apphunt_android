package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import com.apphunt.app.api.AppHuntApiClient;
import com.apphunt.app.api.Callback;
import com.apphunt.app.api.models.SaveApp;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.NotificationsUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;

import it.appspice.android.AppSpice;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SaveAppFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = SaveAppFragment.class.getName();

    private View view;
    private EditText desc;
    private ApplicationInfo data;
    private ActionBarActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_save_app);
        data = getArguments().getParcelable(Constants.KEY_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_save_app, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);
        container.setOnClickListener(this);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(data.loadLabel(activity.getPackageManager()));

        ImageView icon = (ImageView) view.findViewById(R.id.app_icon);
        icon.setImageDrawable(data.loadIcon(activity.getPackageManager()));

        desc = (EditText) view.findViewById(R.id.description);

        Button save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
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
            case R.id.save:
                if (desc.getText() != null && desc.getText().length() >=50) {
                    v.setEnabled(false);
                    SaveApp app = new SaveApp();
                    app.setDescription(desc.getText().toString());
                    app.setPackageName(data.packageName);
                    app.setPlatform(Constants.PLATFORM);
                    app.setUserId(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID));

                    AppHuntApiClient.getClient().saveApp(app, new Callback() {
                        @Override
                        public void success(Object o, Response response) {
                            if (response.getStatus() == 200) {
                                AppSpice.createEvent(TrackingEvents.UserAddedApp).track();
                                activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                NotificationsUtils.showNotificationFragment(activity, getString(R.string.saved_successfully), false, true);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            AppSpice.createEvent(TrackingEvents.UserAddedUnknownApp).track();
                            activity.getSupportFragmentManager().popBackStack();

                            NotificationsUtils.showNotificationFragment(activity, getString(R.string.not_available_in_the_store), false, false);
                            v.setEnabled(true);
                        }
                    });
                } else if (desc.getText() != null && desc.getText().length() > 0 && desc.getText().length() <= 50) {
                    desc.setHint(R.string.hint_short_description);
                    desc.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
                    desc.setError("Min 50 chars");
                    Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(300);
                } else if (desc.getText() == null || desc.getText() != null && desc.getText().length() == 0) {
                    desc.setHint(R.string.hint_please_enter_description);
                    desc.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
                    Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(300);
                }
                break;
            
            case R.id.container:
                closeKeyboard(desc);
                break;
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

        this.activity = (ActionBarActivity) activity;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        closeKeyboard(desc);
    }
}
