package com.apphunt.app.utils;

import com.apphunt.app.constants.Constants;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nmp on 15-11-10.
 */
public class FlurryWrapper {
    public static void logEvent(String eventName) {
        final String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        FlurryAgent.logEvent(eventName, new HashMap<String, String>(){{
            put("userId", userId);
        }});
    }

    public static void logEvent(String eventName, Map<String, String> params) {
        final String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        params.put("userId", userId);
        FlurryAgent.logEvent(eventName, params);
    }
}
