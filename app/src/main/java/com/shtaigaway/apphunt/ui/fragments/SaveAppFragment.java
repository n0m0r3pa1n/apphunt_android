package com.shtaigaway.apphunt.ui.fragments;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.api.AppHuntApiClient;
import com.shtaigaway.apphunt.api.Callback;
import com.shtaigaway.apphunt.api.models.SaveApp;
import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.NotificationsUtils;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                if (desc.getText() != null && desc.getText().length() > 0) {

                    SaveApp app = new SaveApp();
                    app.setDescription(desc.getText().toString());
                    app.setPackageName(data.packageName);
                    app.setPlatform(Constants.PLATFORM);
                    app.setUserId(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID));

                    AppHuntApiClient.getClient().saveApp(app, new Callback() {
                        @Override
                        public void success(Object o, Response response) {
                            if (response.getStatus() == 200) {
                                activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                NotificationsUtils.showNotificationFragment(activity, getString(R.string.saved_successfully), false, true);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            activity.getSupportFragmentManager().popBackStack();

                            NotificationsUtils.showNotificationFragment(activity, getString(R.string.not_available_in_the_store), false, false);

                        }
                    });
                } else {
                    desc.setHint(R.string.hint_please_enter_description);
                    desc.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
                    Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(300);
                }
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (ActionBarActivity) activity;
    }
}
