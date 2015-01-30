package com.shtaigaway.apphunt.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.facebook.Session;
import com.shtaigaway.apphunt.MainActivity;
import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.api.models.User;
import com.shtaigaway.apphunt.ui.fragments.LoginFragment;

public class FacebookUtils {

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
        session.close();
    }

    public static void onLogin(Context ctx, User user) {
        SharedPreferencesHelper.setPreference(ctx, Constants.KEY_USER_ID, user.getId());
        SharedPreferencesHelper.setPreference(ctx, Constants.KEY_EMAIL, user.getEmail());
        SharedPreferencesHelper.setPreference(ctx, Constants.KEY_PROFILE_PICTURE, user.getProfilePicture());
        SharedPreferencesHelper.setPreference(ctx, Constants.KEY_NAME, user.getName());

        ((MainActivity) ctx).onUserLogin();
        hideLoginFragment(ctx);

    }

    public static void onLogout(Context ctx) {
        SharedPreferencesHelper.removePreference(ctx, Constants.KEY_USER_ID);
        SharedPreferencesHelper.removePreference(ctx, Constants.KEY_EMAIL);
        SharedPreferencesHelper.removePreference(ctx, Constants.KEY_PROFILE_PICTURE);
        SharedPreferencesHelper.removePreference(ctx, Constants.KEY_NAME);

        ((MainActivity) ctx).onUserLogout();
    }

    public static void onStart(Context ctx) {
        if (!isSessionOpen()) {
            SharedPreferencesHelper.removePreference(ctx, Constants.KEY_USER_ID);
            SharedPreferencesHelper.removePreference(ctx, Constants.KEY_EMAIL);
            SharedPreferencesHelper.removePreference(ctx, Constants.KEY_PROFILE_PICTURE);
            SharedPreferencesHelper.removePreference(ctx, Constants.KEY_NAME);
        }
    }
}
