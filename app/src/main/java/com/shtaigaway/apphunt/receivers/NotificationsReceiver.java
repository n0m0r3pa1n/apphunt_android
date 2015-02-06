package com.shtaigaway.apphunt.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.NotificationsUtils;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;


public class NotificationsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) || "com.shtaigaway.apphunt.action.ENABLE_NOTIFICATIONS".equals(intent.getAction())) {
            if (SharedPreferencesHelper.getBooleanPreference(ctx, Constants.IS_DAILY_NOTIFICATION_ENABLED)) {
//                NotificationsUtils.setupDailyNotificationService(ctx);
            } else {
//                NotificationsUtils.disableDailyNotificationsService(ctx);
            }
        }
    }
}
