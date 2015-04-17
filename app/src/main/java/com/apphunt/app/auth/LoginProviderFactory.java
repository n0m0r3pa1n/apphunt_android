package com.apphunt.app.auth;

import android.app.Activity;
import android.content.Context;

import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public class LoginProviderFactory {

    private static LoginProvider loginProvider;

    public static LoginProvider get(Activity activity) {
        if (loginProvider == null) {
            return new TwitterLoginProvider(activity);
        }
        return loginProvider;
    }

    public static void setLoginProvider(Context context, LoginProvider loginProvider) {
        LoginProviderFactory.loginProvider = loginProvider;
        SharedPreferencesHelper.setPreference(context, Constants.KEY_LOGIN_PROVIDER, loginProvider.getName());
    }


}
