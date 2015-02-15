package com.apphunt.app.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.LoginFragment;

public class FacebookUtils {

    public static void showLoginFragment(Context ctx) {
        Fragment loginFragment = new LoginFragment();
        ((ActionBarActivity) ctx).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, loginFragment, Constants.TAG_LOGIN_FRAGMENT)
                .addToBackStack(Constants.TAG_LOGIN_FRAGMENT)
                .commit();
    }
}
