package com.apphunt.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;


public class NotificationsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        SharedPreferencesHelper.init(ctx);
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) || ctx.getString(R.string.action_enable_notifications).equals(intent.getAction())) {
            if (SharedPreferencesHelper.getBooleanPreference(Constants.SETTING_NOTIFICATIONS_ENABLED)) {
                NotificationsUtils.setupDailyNotificationService(ctx);
            } else {
                NotificationsUtils.disableDailyNotificationsService(ctx);
            }
        }
    }
}
