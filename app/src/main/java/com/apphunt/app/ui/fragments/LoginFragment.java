package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.apphunt.app.utils.NotificationsUtils;
import com.apphunt.app.utils.TrackingEvents;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.apphunt.app.R;
import com.apphunt.app.api.AppHuntApiClient;
import com.apphunt.app.api.Callback;
import com.apphunt.app.api.models.User;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.FacebookUtils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

import it.appspice.android.AppSpice;
import retrofit.RetrofitError;

public class LoginFragment extends BaseFragment {

    private static final String TAG = LoginFragment.class.getName();

    private View view;

    private UiLifecycleHelper uiHelper;

    private ActionBarActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FacebookUtils.isSessionOpen()) {
            setTitle(R.string.title_logout);
        } else {
            setTitle(R.string.title_login);
        }

        uiHelper = new UiLifecycleHelper(activity, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("email"));

        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_out_top);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Bundle params = new Bundle();
            params.putString("fields", "id,name,email,picture");

            new Request(session, "/me", params, HttpMethod.GET, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    if (response != null) {
                        User user = new User();
                        try {
                            Locale locale = getResources().getConfiguration().locale;

                            JSONObject jsonObject = response.getGraphObject().getInnerJSONObject();
                            user.setLoginId(jsonObject.getString("id"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setName(jsonObject.getString("name"));
                            user.setProfilePicture(jsonObject.getJSONObject("picture").getJSONObject("data").getString("url"));
                            user.setLoginType(Constants.LOGIN_TYPE);
                            user.setLocale(String.format("%s-%s", locale.getCountry().toLowerCase(), locale.getLanguage()).toLowerCase());

                            AppHuntApiClient.getClient().createUser(user, new Callback<User>() {
                                @Override
                                public void success(User user, retrofit.client.Response response) {
                                    if (user != null) {
                                        AppSpice.createEvent(TrackingEvents.UserLoggedIn).track();
                                        FacebookUtils.onLogin(activity, user);
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    NotificationsUtils.showNotificationFragment(activity, getString(R.string.notification_cannot_login) , false, false);
                                }
                            });
                        } catch (Exception e) {
                            NotificationsUtils.showNotificationFragment(activity, getString(R.string.notification_cannot_login) , false, false);
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        NotificationsUtils.showNotificationFragment(activity, getString(R.string.notification_cannot_login) , false, false);
                    }
                }
            }).executeAsync();
        } else if (state.isClosed()) {
            AppSpice.createEvent(TrackingEvents.UserLoggedOut).track();
            FacebookUtils.onLogout(activity);
        }

        activity.supportInvalidateOptionsMenu();
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (ActionBarActivity) activity;
    }


    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
