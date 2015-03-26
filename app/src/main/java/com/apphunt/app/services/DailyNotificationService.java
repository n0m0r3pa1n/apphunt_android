package com.apphunt.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.AppHuntApiClient;
import com.apphunt.app.api.apphunt.Callback;
import com.apphunt.app.api.apphunt.models.Notification;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.appspice.android.AppSpice;
import retrofit.client.Response;


public class DailyNotificationService extends IntentService {
    private static final String TAG = DailyNotificationService.class.getSimpleName();

    public DailyNotificationService() {
        super("DailyNotificationService");
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!ConnectivityUtils.isNetworkAvailable(this)) {
            displayNotification(getNotificationFromSharedPrefs());
        } else {
            getNotificationFromServer();
        }
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();


    private void getNotificationFromServer() {
        final String date = dateFormat.format(calendar.getTime());

        AppSpice.init(this, Constants.APP_SPICE_APP_ID);
        AppHuntApiClient.getClient().getNotification("DailyReminder", new Callback<com.apphunt.app.api.apphunt.models.Notification>() {
            @Override
            public void success(com.apphunt.app.api.apphunt.models.Notification notification, Response response) {
                displayNotification(notification);
                saveNotification(notification);
            }
        });
    }

    private void saveNotification(Notification notification) {
        SharedPreferencesHelper.setPreference(getApplicationContext(), Constants.KEY_NOTIFICATION_TITLE, notification.getTitle());
        SharedPreferencesHelper.setPreference(getApplicationContext(), Constants.KEY_NOTIFICATION_MSG, notification.getMessage());
    }

    private Notification getNotificationFromSharedPrefs() {
        Notification notification = new Notification();

        if (TextUtils.isEmpty(SharedPreferencesHelper.getStringPreference(getApplicationContext(), Constants.KEY_NOTIFICATION_TITLE))) {
            SharedPreferencesHelper.setPreference(getApplicationContext(), Constants.KEY_NOTIFICATION_TITLE, Constants.DEFAULT_NOTIFICATION_TITLE);
            SharedPreferencesHelper.setPreference(getApplicationContext(), Constants.KEY_NOTIFICATION_MSG, Constants.DEFAULT_NOTIFICATION_MSG);
        }

        notification.setTitle(SharedPreferencesHelper.getStringPreference(getApplicationContext(), Constants.KEY_NOTIFICATION_TITLE));
        notification.setMessage(SharedPreferencesHelper.getStringPreference(getApplicationContext(), Constants.KEY_NOTIFICATION_MSG));

        return notification;
    }

    private void displayNotification(Notification notification) {
        try {
            AppSpice.createEvent(TrackingEvents.AppShowedTrendingAppsNotification).track();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        Intent notifyIntent = new Intent(DailyNotificationService.this, MainActivity.class);
        notifyIntent.putExtra(Constants.KEY_DAILY_REMINDER_NOTIFICATION, true);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(DailyNotificationService.this)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getMessage())
                        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 101, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notification.getMessage()))
                        .setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
