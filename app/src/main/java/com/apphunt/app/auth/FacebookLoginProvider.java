package com.apphunt.app.auth;

import android.app.Activity;
import android.os.Bundle;

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
