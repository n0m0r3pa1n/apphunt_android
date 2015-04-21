package com.apphunt.app.utils;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.LoginFragment;

public class LoginUtils {

    public static void showLoginFragment(Context ctx) {
        show(ctx, false);
    }

    public static void showSkippableLoginFragment(Context ctx) {
        show(ctx, true);
    }


    private static void show(Context ctx, boolean canBeSkipped) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setCanBeSkipped(canBeSkipped);
        ((ActionBarActivity) ctx).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, loginFragment, Constants.TAG_LOGIN_FRAGMENT)
                .addToBackStack(Constants.TAG_LOGIN_FRAGMENT)
                .commit();

    }
}
