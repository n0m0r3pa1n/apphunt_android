package com.apphunt.app.auth;

import android.app.Activity;

import com.apphunt.app.api.apphunt.models.users.User;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

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
        LoginManager.getInstance().logOut();
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
}
