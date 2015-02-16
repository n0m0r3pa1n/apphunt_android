package com.apphunt.app.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.AppHuntApiClient;
import com.apphunt.app.api.Callback;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.TrackingEvents;

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
            Log.d(TAG, "No internet in service!");
            return;
        }
        displayNotification();
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();


    private void displayNotification() {
        final String date = dateFormat.format(calendar.getTime());

        AppHuntApiClient.getClient().getNotification("DailyReminder", new Callback<com.apphunt.app.api.models.Notification>() {
            @Override
            public void success(com.apphunt.app.api.models.Notification notification, Response response) {

                AppSpice.createEvent(TrackingEvents.AppShowedTrendingAppsNotification).track();
                Intent notifyIntent = new Intent(DailyNotificationService.this, MainActivity.class);
                notifyIntent.putExtra(Constants.KEY_DAILY_REMINDER_NOTIFICATION, true);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(DailyNotificationService.this)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
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
        });

    }
}
