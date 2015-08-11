package com.apphunt.app.auth;

import android.app.Activity;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

/**
 * Created by nmp on 15-8-10.
 */
public class FacebookLoginProvider extends BaseLoginProvider implements LoginProvider{
    public static final String PROVIDER_NAME = "facebook";

    public FacebookLoginProvider(Activity activity) {
        super(activity);
    }

    @Override
    public void logout() {
        super.logout();
        AccessToken.setCurrentAccessToken(null);
        LoginManager.getInstance().logOut();
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
}
