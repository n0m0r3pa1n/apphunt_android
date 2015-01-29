package com.shtaigaway.apphunt.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.facebook.Session;
import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.ui.fragments.LoginFragment;

public class FacebookUtils {

    private static FacebookUtils instance;

    public static FacebookUtils getInstance() {
        if (instance == null) {
            instance = new FacebookUtils();
        }

        return instance;
    }


    private FacebookUtils() {
    }

    public void showLoginFragment(Context ctx) {
        Fragment loginFragment = new LoginFragment();
        ((ActionBarActivity) ctx).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, loginFragment, Constants.TAG_LOGIN_FRAGMENT)
                .addToBackStack(Constants.TAG_LOGIN_FRAGMENT)
                .commit();
    }

    public void hideLoginFragment(Context ctx) {
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
}
