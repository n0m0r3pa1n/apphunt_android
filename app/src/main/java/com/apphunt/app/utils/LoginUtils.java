package com.apphunt.app.utils;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.ui.fragments.login.LoginFragment;

public class LoginUtils {

    public static void showLoginFragment(Context ctx, boolean canBeSkipped) {
        showLoginFragment(ctx, canBeSkipped, null);
    }

    public static void showLoginFragment(Context ctx, boolean canBeSkipped, int messageRes) {
        showLoginFragment(ctx, canBeSkipped, ctx.getString(messageRes));
    }

    public static void showLoginFragment(Context ctx, boolean canBeSkipped, String message) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setCanBeSkipped(canBeSkipped);
        loginFragment.setMessage(TextUtils.isEmpty(message) ? ctx.getString(R.string.login_info_text) : message);

        ((AppCompatActivity) ctx).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, loginFragment, Constants.TAG_LOGIN_FRAGMENT)
                .addToBackStack(Constants.TAG_LOGIN_FRAGMENT)
                .commit();

    }
}
