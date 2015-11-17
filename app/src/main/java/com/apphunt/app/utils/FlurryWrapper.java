package com.apphunt.app.utils;

import android.content.Context;

import com.apphunt.app.BuildConfig;
import com.apphunt.app.constants.Constants;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nmp on 15-11-10.
 */
public class FlurryWrapper {
    private static String userId;

    public static void init(Context context) {
        if (BuildConfig.DEBUG) {
            FlurryAgent.init(context, Constants.FLURRY_DEBUG_API_KEY);
        } else {
            FlurryAgent.init(context, Constants.FLURRY_API_KEY);
        }
    }

    public static void logEvent(String eventName) {
        final String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        FlurryAgent.logEvent(eventName, new HashMap<String, String>() {{
            put("userId", userId);
        }});
    }

    public static void logEvent(String eventName, Map<String, String> params) {
        final String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        params.put("userId", userId);
        FlurryAgent.logEvent(eventName, params);
    }

    public static void setUserId(String userId) {
        FlurryAgent.setUserId(userId);
    }
}
