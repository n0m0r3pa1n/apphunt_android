package com.apphunt.app.api.apphunt.client;

import android.content.Context;

import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ApiService {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
    private static Calendar calendar = Calendar.getInstance();
    private static ApiService apiService;
    private Context context;

    private ApiService(Context context) {
        this.context = context;
    }
    public static ApiService getInstance(Context context) {
        if(apiService == null) {
            apiService = new ApiService(context);
        }

        return apiService;
    }

    public void loadAppsForToday() {
        calendar = Calendar.getInstance();
        ApiClient.getClient(context).getApps(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), dateFormat.format(calendar.getTime()), 1, 5, Constants.PLATFORM);
    }

    public void loadAppsForPreviousDate() {
        calendar.add(Calendar.DATE, -1);
        String date = dateFormat.format(calendar.getTime());

        ApiClient.getClient(context).getApps(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                date, 1, 5, Constants.PLATFORM);
    }

    public void loadMoreApps(String userId, String date, String platform, int page, int pageSize) {
        ApiClient.getClient(context).getApps(userId, date, page, pageSize, platform);
    }

    public void loadAppDetails(String userId, String appId) {
        ApiClient.getClient(context).getDetailedApp(userId, appId);
    }

    public void loadAppComments(String appId, String userId, int page, int pageSize) {
        ApiClient.getClient(context).getAppComments(appId, userId, page, pageSize, false);
    }

    public void reloadAppComments(String applicationId) {
        ApiClient.getClient(context).getAppComments(applicationId, SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), 1, 3, true);
    }
}
