package com.apphunt.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Response;
import com.apphunt.app.MainActivity;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;


public class DailyNotificationService extends IntentService {
    private static final String TAG = DailyNotificationService.class.getSimpleName();

    public DailyNotificationService() {
        super("DailyNotificationService");
        setIntentRedelivery(true);
        SharedPreferencesHelper.init(this);
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        if (!ConnectivityUtils.isNetworkAvailable(this)) {
            displayNotification(getNotificationFromSharedPrefs());
        } else {
            getNotificationFromServer();
        }
    }

    private void getNotificationFromServer() {
        String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        if (!TextUtils.isEmpty(userId)) {
            FlurryAgent.setUserId(userId);
        }
        FlurryAgent.init(this, Constants.FLURRY_API_KEY);
        ApiClient.getClient(this).getNotification("DailyReminder", new Response.Listener<Notification>() {
            @Override
            public void onResponse(Notification response) {
                displayNotification(response);
                saveNotification(response);
            }
        });
    }

    private void saveNotification(Notification notification) {
        SharedPreferencesHelper.setPreference(Constants.KEY_NOTIFICATION_TITLE, notification.getTitle());
        SharedPreferencesHelper.setPreference(Constants.KEY_NOTIFICATION_MSG, notification.getMessage());
    }

    private Notification getNotificationFromSharedPrefs() {
        Notification notification = new Notification();

        if (TextUtils.isEmpty(SharedPreferencesHelper.getStringPreference(Constants.KEY_NOTIFICATION_TITLE))) {
            SharedPreferencesHelper.setPreference(Constants.KEY_NOTIFICATION_TITLE, Constants.DEFAULT_NOTIFICATION_TITLE);
            SharedPreferencesHelper.setPreference(Constants.KEY_NOTIFICATION_MSG, Constants.DEFAULT_NOTIFICATION_MSG);
        }

        notification.setTitle(SharedPreferencesHelper.getStringPreference(Constants.KEY_NOTIFICATION_TITLE));
        notification.setMessage(SharedPreferencesHelper.getStringPreference(Constants.KEY_NOTIFICATION_MSG));

        return notification;
    }

    private void displayNotification(Notification notification) {
        notification.setType("dailyReminder");
        NotificationsUtils.displayNotification(this, MainActivity.class, notification);
        Map<String, String> params = new HashMap<>();
        params.put("type", notification.getType());
        FlurryAgent.logEvent(TrackingEvents.AppShowedNotification, params);
    }
}
