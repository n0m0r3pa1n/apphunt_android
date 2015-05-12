package com.apphunt.app.auth;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.apphunt.app.MainActivity;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.fragments.LoginFragment;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;

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
        BusProvider.getInstance().post(new LogoutEvent());
        ((MainActivity) activity).onUserLogout();
        hideLoginFragment(activity);
    }

    @Override
    public boolean isUserLoggedIn() {
        String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        String loginProvider = SharedPreferencesHelper.getStringPreference(Constants.KEY_LOGIN_PROVIDER);
        return !TextUtils.isEmpty(loginProvider) && !TextUtils.isEmpty(userId);
    }

    protected void removeSharedPreferences(Activity activity) {
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_ID);
        SharedPreferencesHelper.removePreference(Constants.KEY_EMAIL);
        SharedPreferencesHelper.removePreference(Constants.KEY_LOGIN_PROVIDER);
    }

    @Override
    public void login(User user) {
        onUserCreated(user);
        BusProvider.getInstance().post(new LoginEvent(user));
    }

    private void onUserCreated(User user) {
        FlurryAgent.logEvent(TrackingEvents.UserLoggedIn);
        saveSharedPreferences(activity, user);
        hideLoginFragment(activity);
    }

    protected void saveSharedPreferences(Activity activity, User user) {
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_ID, user.getId());
        SharedPreferencesHelper.setPreference(Constants.KEY_EMAIL, user.getEmail());
        SharedPreferencesHelper.setPreference(Constants.KEY_PROFILE_IMAGE, user.getProfilePicture());
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
