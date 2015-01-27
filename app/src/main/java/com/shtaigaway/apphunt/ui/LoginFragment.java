package com.shtaigaway.apphunt.ui;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;
import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.api.AppHuntApi;
import com.shtaigaway.apphunt.api.AppHuntApiClient;
import com.shtaigaway.apphunt.api.Callback;
import com.shtaigaway.apphunt.api.models.User;
import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.FacebookUtils;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private View view;

    private UiLifecycleHelper uiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_about_me", "email"));

        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_top);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");

            Bundle params = new Bundle();
            params.putString("fields", "name,email,picture");

            new Request(session, "/me", params, HttpMethod.GET, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    if (response != null) {
                        User user = new User();
                        try {
                            JSONObject jsonObject = response.getGraphObject().getInnerJSONObject();
                            user.setEmail(jsonObject.getString("email"));
                            user.setName(jsonObject.getString("name"));
                            user.setProfilePicture(jsonObject.getJSONObject("picture").getJSONObject("data").getString("url"));
                            user.setLoginType(Constants.LOGIN_TYPE);

                            AppHuntApiClient.getClient().createUser(user, new Callback<User>() {
                                @Override
                                public void success(User user, retrofit.client.Response response) {
                                    if (user != null) {
                                        SharedPreferencesHelper.setPreference(getActivity(), Constants.KEY_USER_ID, user.getId());
                                        SharedPreferencesHelper.setPreference(getActivity(), Constants.KEY_EMAIL, user.getEmail());
                                        SharedPreferencesHelper.setPreference(getActivity(), Constants.KEY_PROFILE_PICTURE, user.getProfilePicture());
                                        SharedPreferencesHelper.setPreference(getActivity(), Constants.KEY_NAME, user.getName());
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
            }).executeAsync();
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }

        getActivity().supportInvalidateOptionsMenu();
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

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
    public void onDetach() {
        super.onDetach();

        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
