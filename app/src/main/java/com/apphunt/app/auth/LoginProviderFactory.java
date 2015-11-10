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
public class LoginProviderFactory {
    private static final String TAG = LoginProviderFactory.class.getSimpleName();

    private static LoginProvider loginProvider;
    private static Context context;

    public static LoginProvider get(Context context) {
        try {
            Class<?> clss = Class.forName(SharedPreferencesHelper.getStringPreference(Constants.KEY_LOGIN_PROVIDER_CLASS));
            Constructor<?> constr = clss.getConstructor(Context.class);
            loginProvider = (BaseLoginProvider) constr.newInstance(context);
        } catch (Exception e) {
            Log.e(TAG, "Cannot create this login provider.");
        }

        return loginProvider;
    }

    public static void setLoginProvider(String provider) {
        SharedPreferencesHelper.setPreference(Constants.KEY_LOGIN_PROVIDER_CLASS, provider);
    }

    public static void onCreate(Context context) {
        String loginProviderName = SharedPreferencesHelper.getStringPreference(Constants.KEY_LOGIN_PROVIDER_CLASS);
        LoginProviderFactory.context = context;
        if(TextUtils.isEmpty(loginProviderName) || loginProviderName.equals(AnonymousLoginProvider.PROVIDER_NAME)) {
            createAnonymousProvider();
        }
    }

    public static void reset() {
        createAnonymousProvider();
    }

    private static void createAnonymousProvider() {
        loginProvider = new AnonymousLoginProvider(context);
        ((AnonymousLoginProvider) loginProvider).createAnonymousUser();
        setLoginProvider(AnonymousLoginProvider.class.getCanonicalName());
    }
}
