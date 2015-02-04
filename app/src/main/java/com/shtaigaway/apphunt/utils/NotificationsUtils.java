package com.shtaigaway.apphunt.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.shtaigaway.apphunt.services.DailyNotificationService;

import java.util.Calendar;

public class NotificationsUtils {

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

}
