package com.apphunt.app.auth;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.apphunt.app.MainActivity;
import com.apphunt.app.api.AppHuntApiClient;
import com.apphunt.app.api.Callback;
import com.apphunt.app.api.models.User;
import com.apphunt.app.ui.fragments.LoginFragment;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;

import it.appspice.android.AppSpice;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public abstract class BaseLoginProvider implements LoginProvider {

    private final Activity activity;

    public BaseLoginProvider(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void logout() {
        removeSharedPreferences(activity);
        ((MainActivity) activity).onUserLogout();
        hideLoginFragment(activity);
    }

    @Override
    public boolean isUserLoggedIn() {
        String userId = SharedPreferencesHelper.getStringPreference(getActivity(), Constants.KEY_USER_ID);
        String loginProvider = SharedPreferencesHelper.getStringPreference(getActivity(), Constants.KEY_LOGIN_PROVIDER);

        boolean isLoggedIn = !TextUtils.isEmpty(loginProvider) && !TextUtils.isEmpty(userId);

        return isLoggedIn;
    }

    protected void removeSharedPreferences(Activity activity) {
        SharedPreferencesHelper.removePreference(activity, Constants.KEY_USER_ID);
        SharedPreferencesHelper.removePreference(activity, Constants.KEY_EMAIL);
        SharedPreferencesHelper.removePreference(activity, Constants.KEY_LOGIN_PROVIDER);
    }

    @Override
    public void login(User user) {
        AppHuntApiClient.getClient().createUser(user, new Callback<User>() {
            @Override
            public void success(User user, retrofit.client.Response response) {
                onUserCreated(user);
            }
        });
    }

    private void onUserCreated(User user) {
        AppSpice.createEvent(TrackingEvents.UserLoggedIn).track();
        saveSharedPreferences(activity, user);

        ((MainActivity) activity).onUserLogin();
        ((MainActivity) activity).supportInvalidateOptionsMenu();
        hideLoginFragment(activity);
    }

    protected void saveSharedPreferences(Activity activity, User user) {
        SharedPreferencesHelper.setPreference(activity, Constants.KEY_USER_ID, user.getId());
        SharedPreferencesHelper.setPreference(activity, Constants.KEY_EMAIL, user.getEmail());
    }

    private void hideLoginFragment(Context ctx) {
        FragmentManager fragmentManager = ((ActionBarActivity) ctx).getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(Constants.TAG_LOGIN_FRAGMENT);

        if (loginFragment != null) {
            fragmentManager.popBackStack();
        }
    }

    protected Activity getActivity() {
        return activity;
    }
}
