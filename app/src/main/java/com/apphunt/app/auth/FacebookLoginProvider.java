package com.apphunt.app.auth;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.facebook.Session;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public class FacebookLoginProvider extends BaseLoginProvider implements LoginProvider {
    public static final String PROVIDER_NAME = "facebook";

    public FacebookLoginProvider(Activity activity) {
        super(activity);
    }

    @Override
    public void logout() {
        Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();
        super.logout();
    }

    @Override
    public boolean isUserLoggedIn() {
        String userId = SharedPreferencesHelper.getStringPreference(getActivity(), Constants.KEY_USER_ID);
        String loginProvider = SharedPreferencesHelper.getStringPreference(getActivity(), Constants.KEY_LOGIN_PROVIDER);

        boolean isLoggedIn = !TextUtils.isEmpty(loginProvider) && !TextUtils.isEmpty(userId);
        
        Session session = Session.getActiveSession();
        return (session != null && session.isOpened());
    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        Session.openActiveSessionFromCache(activity);

        if (!isUserLoggedIn()) {
            removeSharedPreferences(activity);
        }
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
}
