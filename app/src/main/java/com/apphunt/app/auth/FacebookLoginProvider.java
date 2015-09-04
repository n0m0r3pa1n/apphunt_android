package com.apphunt.app.auth;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.apphunt.app.auth.models.Friend;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nmp on 15-8-10.
 */
public class FacebookLoginProvider extends BaseLoginProvider implements LoginProvider{
    private static final String TAG = FacebookLoginProvider.class.getSimpleName();
    public static final String PROVIDER_NAME = "facebook";

    private OnFriendsResultListener listener;

    public FacebookLoginProvider(Activity activity) {
        super(activity);
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
