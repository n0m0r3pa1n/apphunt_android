package com.apphunt.app.auth;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;

import java.lang.reflect.Constructor;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public abstract class LoginProviderFactory {
    private static final String TAG = LoginProviderFactory.class.getSimpleName();

    private static LoginProvider loginProvider;

    public static void setLoginProvider(LoginProvider provider) {
        loginProvider = provider;
        SharedPreferencesHelper.setPreference(Constants.KEY_LOGIN_PROVIDER_CLASS, provider.getName());
    }

    public static LoginProvider get(Context context) {
        String loginProviderName = SharedPreferencesHelper.getStringPreference(Constants.KEY_LOGIN_PROVIDER_CLASS, null);
        if(loginProvider == null && !TextUtils.isEmpty(loginProviderName)) {
            try {
                Class<?> clss = Class.forName(loginProviderName);
                Constructor<?> constr = clss.getConstructor(Context.class);
                loginProvider = (BaseLoginProvider) constr.newInstance(context);
            } catch (Exception e) {
                Log.e(TAG, "Cannot create this login provider.");
            }
        }

        return loginProvider;
    }
}
