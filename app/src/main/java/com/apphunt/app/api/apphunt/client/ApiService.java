package com.apphunt.app.api.apphunt.client;

import android.content.Context;

import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ApiService {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
    private static Calendar calendar = Calendar.getInstance();

    public static void loadAppsForToday(Context context) {
        calendar = Calendar.getInstance();
        String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        ApiClient.getClient(context).getApps(userId, dateFormat.format(calendar.getTime()), 1, 5, Constants.PLATFORM);
    }

    public static void loadAppsForPreviousDate(Context context) {
        calendar.add(Calendar.DATE, -1);
        String date = dateFormat.format(calendar.getTime());

        ApiClient.getClient(context).getApps(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                date, 1, 5, Constants.PLATFORM);
    }

    public static void loadMoreApps(Context context, String userId, String date, String platform, int page, int pageSize) {
        ApiClient.getClient(context).getApps(userId, date, page, pageSize, platform);
    }

    public static void loadAppDetails(Context context, String userId, String appId) {
        ApiClient.getClient(context).getDetailedApp(userId, appId);
    }
}
