package com.apphunt.app.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.LoginFragment;
import com.apphunt.app.ui.interfaces.OnUserAuthListener;
import com.apphunt.app.ui.interfaces.UserLoginScreenListener;

import java.util.ArrayList;
import java.util.List;

public class LoginUtils {

    public static void showLoginFragment(Context ctx) {
        show(ctx, false, null);
    }

    public static void showSkippableLoginFragment(Context ctx) {
        show(ctx, true, null);
    }

    public static void showLoginFragment(Context ctx, UserLoginScreenListener listener) {
        show(ctx, false, listener);
    }

    public static void showSkippableLoginFragment(Context ctx, UserLoginScreenListener listener) {
        show(ctx, true, listener);
    }

    private static void show(Context ctx, boolean canBeSkipped, UserLoginScreenListener listener) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setCanBeSkipped(canBeSkipped);
        if(listener != null) {
            loginFragment.setUserLoginScreenListener(listener);
        }
        ((ActionBarActivity) ctx).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, loginFragment, Constants.TAG_LOGIN_FRAGMENT)
                .addToBackStack(Constants.TAG_LOGIN_FRAGMENT)
                .commit();

    }
}
