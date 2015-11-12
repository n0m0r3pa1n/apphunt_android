package com.apphunt.app.auth;

import android.content.Context;
import android.text.TextUtils;

import com.apphunt.app.api.apphunt.models.users.LoginType;
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

    public static final String PROVIDER_NAME = AnonymousLoginProvider.class.getCanonicalName();

    public AnonymousLoginProvider(Context context) {
        super(context);
        if(TextUtils.isEmpty(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID))) {
            createAnonymousUser();
        }
    }

    public void createAnonymousUser() {
        final User anonymousUser = new User();
        anonymousUser.setName("Anonymous");
        anonymousUser.setUsername("Anonymous");
        anonymousUser.setProfilePicture("http://i.imgur.com/wYdyc9s.png");


        if(TextUtils.isEmpty(anonymousUser.getAdvertisingId())) {
            GoogleUtils.obtainAdvertisingId(context, new GoogleUtils.OnResultListener() {
                @Override
                public void onResultReady(String advertisingId) {
                    anonymousUser.setAdvertisingId(advertisingId);
                    AnonymousLoginProvider.super.login(anonymousUser);
                }
            });
        } else {
            AnonymousLoginProvider.super.login(anonymousUser);
        }
    }

    @Override
    public void logout() {
        removeSharedPreferences();
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
    public String getLoginType() {
        return LoginType.Anonymous.toString();
    }
}
