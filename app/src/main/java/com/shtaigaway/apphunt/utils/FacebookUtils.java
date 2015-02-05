package com.shtaigaway.apphunt.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.facebook.Session;
import com.shtaigaway.apphunt.MainActivity;
import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.api.models.User;
import com.shtaigaway.apphunt.ui.fragments.LoginFragment;

public class FacebookUtils {

    private static final String TAG = FacebookUtils.class.getName();

    public static void showLoginFragment(Context ctx) {
        Fragment loginFragment = new LoginFragment();
        ((ActionBarActivity) ctx).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, loginFragment, Constants.TAG_LOGIN_FRAGMENT)
                .addToBackStack(Constants.TAG_LOGIN_FRAGMENT)
                .commit();
    }

    public static void hideLoginFragment(Context ctx) {
        FragmentManager fragmentManager = ((ActionBarActivity) ctx).getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(Constants.TAG_LOGIN_FRAGMENT);

        if (loginFragment != null) {
            fragmentManager.popBackStack();
        }
    }

    public static boolean isSessionOpen() {
        Session session = Session.getActiveSession();
        return (session != null && session.isOpened());
    }

    public static void closeSession() {
        Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();
    }

    public static void onLogin(Activity activity, User user) {
        SharedPreferencesHelper.setPreference(activity, Constants.KEY_USER_ID, user.getId());
        SharedPreferencesHelper.setPreference(activity, Constants.KEY_EMAIL, user.getEmail());
        SharedPreferencesHelper.setPreference(activity, Constants.KEY_PROFILE_PICTURE, user.getProfilePicture());
        SharedPreferencesHelper.setPreference(activity, Constants.KEY_NAME, user.getName());

        ((MainActivity) activity).onUserLogin();
        hideLoginFragment(activity);
    }

    public static void onLogout(Context ctx) {
        removeSharedPreferences(ctx);
        closeSession();

        hideLoginFragment(ctx);
        ((MainActivity) ctx).onUserLogout();
    }

    public static void onStart(Context ctx) {
        Session.openActiveSessionFromCache(ctx);

        if (!isSessionOpen()) {
            removeSharedPreferences(ctx);
        }
    }

    private static void removeSharedPreferences(Context ctx) {
        SharedPreferencesHelper.removePreference(ctx, Constants.KEY_USER_ID);
        SharedPreferencesHelper.removePreference(ctx, Constants.KEY_EMAIL);
        SharedPreferencesHelper.removePreference(ctx, Constants.KEY_PROFILE_PICTURE);
        SharedPreferencesHelper.removePreference(ctx, Constants.KEY_NAME);
    }
}
