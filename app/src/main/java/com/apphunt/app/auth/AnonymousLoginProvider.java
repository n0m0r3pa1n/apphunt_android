package com.apphunt.app.auth;

import android.content.Context;

import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.GoogleUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 11/3/15.
 * *
 * * NaughtySpirit 2015
 */
public class AnonymousLoginProvider extends BaseLoginProvider {
    private static final String TAG = AnonymousLoginProvider.class.getSimpleName();

    public static final String PROVIDER_NAME = "anonymous";

    public AnonymousLoginProvider(Context context) {
        super(context);
    }

    public void createAnonymousUser() {
        final User anonymousUser = new User();
        anonymousUser.setName("Anonymous");
        anonymousUser.setUsername("Anonymous");
        anonymousUser.setProfilePicture("http://i.imgur.com/wYdyc9s.png");
        anonymousUser.setLoginType(PROVIDER_NAME);

        GoogleUtils.obtainAdvertisingId(context, new GoogleUtils.OnResultListener() {
            @Override
            public void onResultReady(String advertisingId) {
                anonymousUser.setAdvertisingId(advertisingId);
                ApiClient.getClient(context).createAnonymousUser(anonymousUser);
            }
        });
    }

    @Override
    public void login(User user) {
        saveSharedPreferences(user);
    }

    @Override
    public void logout() {
        removeSharedPreferences();
    }

    @Override
    public User getUser() {
        User user = new User();
        user.setId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID, ""));
        user.setName(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_NAME, ""));
        user.setAdvertisingId(SharedPreferencesHelper.getStringPreference(Constants.KEY_ADVERTISING_ID, ""));
        user.setLoginType(Constants.KEY_LOGIN_PROVIDER);

        return user;
    }

    @Override
    public boolean isUserLoggedIn() {
        return false;
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public void loadFriends(BaseLoginProvider.OnFriendsResultListener listener) {
    }

    protected void saveSharedPreferences(User user) {
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_ID, user.getId());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_NAME, user.getName());
        SharedPreferencesHelper.setPreference(Constants.KEY_ADVERTISING_ID, user.getAdvertisingId());
        SharedPreferencesHelper.setPreference(Constants.KEY_LOGIN_PROVIDER, user.getLoginType());
        SharedPreferencesHelper.setPreference(Constants.KEY_LOGIN_PROVIDER_CLASS, AnonymousLoginProvider.class.getCanonicalName());
    }

    protected void removeSharedPreferences() {
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_ID);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_NAME);
        SharedPreferencesHelper.removePreference(Constants.KEY_ADVERTISING_ID);
        SharedPreferencesHelper.removePreference(Constants.KEY_LOGIN_PROVIDER);
        SharedPreferencesHelper.removePreference(Constants.KEY_LOGIN_PROVIDER_CLASS);
    }
}
