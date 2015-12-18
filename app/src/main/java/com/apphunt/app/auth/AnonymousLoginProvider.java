package com.apphunt.app.auth;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.apphunt.app.api.apphunt.models.users.LoginType;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.tracker.EventTracker;
import com.apphunt.app.utils.GoogleUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;

import java.util.Arrays;

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

    @Override
    public void logout() {
        removeSharedPreferences();
        EventTracker.getInstance().resetActions();
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

    public void createAnonymousUser() {
        final User anonymousUser = new User();
        anonymousUser.setName("Anonymous");
        anonymousUser.setUsername("Anonymous");
        anonymousUser.setProfilePicture("http://i.imgur.com/wYdyc9s.png");


        if(TextUtils.isEmpty(anonymousUser.getAdvertisingId())) {
            GoogleUtils.obtainAdvertisingId(context, new GoogleUtils.OnResultListener() {
                @Override
                public void onResultReady(String advertisingId) {
                    if(TextUtils.isEmpty(advertisingId)) {
                        advertisingId = getDeviceId(context);
                        Log.d(TAG, "onResultReady: " + advertisingId);
                    }

                    anonymousUser.setAdvertisingId(advertisingId);
                    AnonymousLoginProvider.super.login(anonymousUser);
                }
            });
        } else {
            AnonymousLoginProvider.super.login(anonymousUser);
        }
    }

    public String getDeviceId(Context context) {
        String id = getUniqueID(context);
        if (id == null)
            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }

    private String getUniqueID(Context context) {

        String telephonyDeviceId = "NoTelephonyId";
        String androidDeviceId = "NoAndroidId";

        // get telephony id
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyDeviceId = tm.getDeviceId();
            if (telephonyDeviceId == null) {
                telephonyDeviceId = "NoTelephonyId";
            }
        } catch (Exception e) {
        }

        // get internal android device id
        try {
            androidDeviceId = android.provider.Settings.Secure.getString(context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            if (androidDeviceId == null) {
                androidDeviceId = "NoAndroidId";
            }
        } catch (Exception e) {

        }

        // build up the uuid
        try {
            String id = getStringIntegerHexBlocks(androidDeviceId.hashCode())
                    + "-"
                    + getStringIntegerHexBlocks(telephonyDeviceId.hashCode());

            return id;
        } catch (Exception e) {
            return "0000-0000-1111-1111";
        }
    }

    public String getStringIntegerHexBlocks(int value) {
        String result = "";
        String string = Integer.toHexString(value);

        int remain = 8 - string.length();
        char[] chars = new char[remain];
        Arrays.fill(chars, '0');
        string = new String(chars) + string;

        int count = 0;
        for (int i = string.length() - 1; i >= 0; i--) {
            count++;
            result = string.substring(i, i + 1) + result;
            if (count == 4) {
                result = "-" + result;
                count = 0;
            }
        }

        if (result.startsWith("-")) {
            result = result.substring(1, result.length());
        }

        return result;
    }
}
