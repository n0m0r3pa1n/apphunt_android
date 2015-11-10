package com.apphunt.app.auth;

import android.content.Context;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

/**
 * Created by nmp on 15-8-10.
 */
public class FacebookLoginProvider extends BaseLoginProvider implements LoginProvider{
    private static final String TAG = FacebookLoginProvider.class.getSimpleName();
    public static final String PROVIDER_NAME = "facebook";

    private OnFriendsResultListener listener;

    public FacebookLoginProvider(Context context) {
        super(context);
    }

    @Override
    public void loadFriends(final OnFriendsResultListener listener) {
        super.loadFriends(listener);
        this.listener = listener;
    }

    @Override
    public void logout() {
        AccessToken.setCurrentAccessToken(null);
        Profile.setCurrentProfile(null);
        LoginManager.getInstance().logOut();
        super.logout();
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
}
