package com.apphunt.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.apphunt.app.R;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.NotificationsUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;


public class NotificationsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) || ctx.getString(R.string.action_enable_notifications).equals(intent.getAction())) {
            if (SharedPreferencesHelper.getBooleanPreference(ctx, Constants.IS_DAILY_NOTIFICATION_ENABLED)) {
                NotificationsUtils.setupDailyNotificationService(ctx);
            } else {
                NotificationsUtils.disableDailyNotificationsService(ctx);
            }
        }
    }
}
