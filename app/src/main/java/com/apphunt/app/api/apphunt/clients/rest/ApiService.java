package com.apphunt.app.api.apphunt.clients.rest;

import android.content.Context;
import android.util.Log;

import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ApiService {
    public static final String TAG = ApiService.class.getSimpleName();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
    private static Calendar calendar = Calendar.getInstance();
    private static Calendar historyCalendar = Calendar.getInstance();
    private static ApiService apiService;

    private int currentPage = 0;
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

    public void reloadApps() {
        calendar = Calendar.getInstance();
        ApiClient.getClient(context).cancelAllRequests();
        currentPage = 0;
        loadApps(false);
    }

    public void loadApps(boolean shouldChangeDate) {
        if(shouldChangeDate) {
            currentPage = 1;
            calendar.add(Calendar.DATE, -1);
        } else {
            currentPage++;
        }

        Log.d(TAG, "loadApps date " +dateFormat.format(calendar.getTime()) + "page " + currentPage);

        ApiClient.getClient(context).getApps(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                dateFormat.format(calendar.getTime()), currentPage, Constants.PAGE_SIZE, Constants.PLATFORM);
    }

    public void loadHistoryForToday() {
        historyCalendar = Calendar.getInstance();
        ApiClient.getClient(context).getUserHistory(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), historyCalendar.getTime());
    }

    public void loadHistoryForPreviousDate() {
        historyCalendar.add(Calendar.DATE, -1);
        String date = dateFormat.format(historyCalendar.getTime());
        ApiClient.getClient(context).getUserHistory(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), historyCalendar.getTime());
    }

    public void loadAppDetails(String userId, String appId) {
        ApiClient.getClient(context).getDetailedApp(userId, appId);
    }

    public void loadAppComments(String appId, String userId, int page, int pageSize) {
        ApiClient.getClient(context).getAppComments(appId, userId, page, pageSize, false);
    }

    public void reloadAppComments(String applicationId) {
        ApiClient.getClient(context).getAppComments(applicationId,
                SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), 1,
                Constants.COMMENTS_PAGE_SIZE, true);
    }

    public static void resetCalendars() {
        calendar = Calendar.getInstance();
        historyCalendar = Calendar.getInstance();
    }
}
