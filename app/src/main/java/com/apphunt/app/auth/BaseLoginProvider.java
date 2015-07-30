package com.apphunt.app.auth;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.fragments.notification.LoginFragment;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.constants.TrackingEvents;
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
        removeSharedPreferences();
        BusProvider.getInstance().post(new LogoutEvent());
        hideLoginFragment(activity);
    }

    @Override
    public void login(User user) {
        onUserCreated(user);
        BusProvider.getInstance().post(new LoginEvent(user));
    }

    @Override
    public User getUser() {
        User user = new User();
        user.setId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
        user.setEmail(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_EMAIL));
        user.setProfilePicture(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_PROFILE_PICTURE));
        user.setName(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_NAME));
        user.setUsername(SharedPreferencesHelper.getStringPreference(Constants.KEY_USERNAME));
        user.setCoverPicture(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_COVER_PICTURE));

        return user;
    }

    @Override
    public boolean isUserLoggedIn() {
        String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        String loginProvider = SharedPreferencesHelper.getStringPreference(Constants.KEY_LOGIN_PROVIDER);
        return !TextUtils.isEmpty(loginProvider) && !TextUtils.isEmpty(userId);
    }

    private void onUserCreated(User user) {
        FlurryAgent.logEvent(TrackingEvents.UserLoggedIn);
        saveSharedPreferences(user);
        hideLoginFragment(activity);
    }

    protected void saveSharedPreferences(User user) {
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_ID, user.getId());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_EMAIL, user.getEmail());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_NAME, user.getName());
        SharedPreferencesHelper.setPreference(Constants.KEY_USERNAME, user.getUsername());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_PROFILE_PICTURE, user.getProfilePicture());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_COVER_PICTURE, user.getCoverPicture());
    }

    protected void removeSharedPreferences() {
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_ID);
        SharedPreferencesHelper.removePreference(Constants.KEY_USERNAME);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_NAME);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_EMAIL);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_PROFILE_PICTURE);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_COVER_PICTURE);
        SharedPreferencesHelper.removePreference(Constants.KEY_LOGIN_PROVIDER);
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
