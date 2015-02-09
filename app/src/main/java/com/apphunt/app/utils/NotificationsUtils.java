package com.apphunt.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.apphunt.app.R;
import com.apphunt.app.api.models.Notification;
import com.apphunt.app.services.DailyNotificationService;
import com.apphunt.app.ui.fragments.NotificationFragment;

import java.util.Calendar;

public class NotificationsUtils {

    private static final String TAG = Notification.class.getName();

    public static void setupDailyNotificationService(Context ctx) {
        Intent intent = new Intent(ctx, DailyNotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(ctx, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        if(alarmMgr == null || alarmIntent == null) {
            return;
        }

        Calendar today = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 20);
        calendar.set(Calendar.SECOND, 0);

        if (today.after(calendar)) {
            calendar.add(Calendar.DATE, +1);
        }

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        SharedPreferencesHelper.setPreference(ctx, Constants.IS_DAILY_NOTIFICATION_ENABLED, true);
    }

    public static void disableDailyNotificationsService(Context ctx) {
        Intent intent = new Intent(ctx, DailyNotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(ctx, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);

        SharedPreferencesHelper.setPreference(ctx, Constants.IS_DAILY_NOTIFICATION_ENABLED, false);
    }

    public static void showNotificationFragment(ActionBarActivity activity, String message, boolean showSettingsAction, boolean showRating) {
        try {
            Bundle extras = new Bundle();
            extras.putString(Constants.KEY_NOTIFICATION, message);
            extras.putBoolean(Constants.KEY_SHOW_SETTINGS, showSettingsAction);
            extras.putBoolean(Constants.KEY_SHOW_RATING, showRating);
            NotificationFragment notificationFragment = new NotificationFragment();
            notificationFragment.setArguments(extras);

            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in, R.anim.slide_out_top)
                    .add(R.id.container, notificationFragment, Constants.TAG_NOTIFICATION_FRAGMENT)
                    .addToBackStack(Constants.TAG_NOTIFICATION_FRAGMENT)
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Cannot present the notification fragment.\nError: " + e.getMessage());
        }

    }
}
