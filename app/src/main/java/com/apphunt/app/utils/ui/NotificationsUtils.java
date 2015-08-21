package com.apphunt.app.utils.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.services.DailyNotificationService;
import com.apphunt.app.ui.fragments.notification.NotificationFragment;
import com.apphunt.app.ui.interfaces.OnActionNeeded;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.crashlytics.android.Crashlytics;

import java.util.Calendar;

import kr.nectarine.android.fruitygcm.FruityGcmClient;
import kr.nectarine.android.fruitygcm.interfaces.FruityGcmListener;

public class NotificationsUtils {

    private static final String TAG = Notification.class.getName();
    public static final int NOTIFICATION_SOUND_START_HOUR = 10;
    public static final int NOTIFICATION_SOUND_END_HOUR = 22;

    public static void setupDailyNotificationService(Context ctx) {

        Intent intent = new Intent(ctx, DailyNotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(ctx, Constants.RC_DAILY_SERVICE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        if (alarmMgr == null || alarmIntent == null) {
            return;
        }

        Calendar today = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (today.after(calendar)) {
            calendar.add(Calendar.DATE, +1);
        }

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        SharedPreferencesHelper.setPreference(Constants.SETTING_NOTIFICATIONS_ENABLED, true);
    }

    public static void disableDailyNotificationsService(Context ctx) {
        Intent intent = new Intent(ctx, DailyNotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(ctx, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);

        SharedPreferencesHelper.setPreference(Constants.SETTING_NOTIFICATIONS_ENABLED, false);
    }

    public static void showNotificationFragmentWithContinueAction(ActionBarActivity activity, String message, OnActionNeeded actionListener) {
        showNotificationFragment(activity, message, false, true, false, true, actionListener);
    }

    public static void showNotificationFragment(ActionBarActivity activity, String message, boolean showSettingsAction, boolean showRating) {
        showNotificationFragment(activity, message, showSettingsAction, false, showRating, true, null);
    }

    private static void showNotificationFragment(ActionBarActivity activity, String message, boolean showSettingsAction, boolean showContinueAction,
                                                 boolean showRating, boolean showShadow, OnActionNeeded actionListener) {
        try {
            Bundle extras = new Bundle();
            extras.putString(Constants.KEY_NOTIFICATION, message);
            extras.putBoolean(Constants.KEY_SHOW_SETTINGS, showSettingsAction);
            extras.putBoolean(Constants.KEY_SHOW_CONTINUE, showContinueAction);
            extras.putBoolean(Constants.KEY_SHOW_RATING, showRating);
            extras.putBoolean(Constants.KEY_SHOW_SHADOW, showShadow);
            NotificationFragment notificationFragment = new NotificationFragment();
            notificationFragment.setArguments(extras);
            notificationFragment.setActionListener(actionListener);

            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in, R.anim.slide_out_top)
                    .add(R.id.container, notificationFragment, Constants.TAG_NOTIFICATION_FRAGMENT)
                    .addToBackStack(Constants.TAG_NOTIFICATION_FRAGMENT)
                    .commit();
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.e(TAG, "Cannot present the notification fragment.\nError: " + e.getMessage());
        }
    }

    public static void displayNotification(Context context, Class targetClass, Bundle extras,
                                           Notification notification, Bitmap largeIcon) {

        Intent notifyIntent = new Intent(context, targetClass);
        if(extras == null) {
            extras = new Bundle();
        }
        extras.putString(Constants.KEY_NOTIFICATION_TYPE, notification.getType());
        notifyIntent.putExtras(extras);

        if (largeIcon == null) {
            largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        }
        try {
            Integer notificationCode = Constants.NOTIFICATION_TYPE_TO_REQUEST_CODE.get(notification.getType());
            notificationCode = notificationCode == null ? 200 : notificationCode;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setDefaults(getDefaults())
                            .setSmallIcon(R.drawable.ic_small_notification)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(notification.getTitle())
                            .setContentText(notification.getMessage())
                            .setContentIntent(PendingIntent.getActivity(context, notificationCode, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(notification.getMessage()))
                            .setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationCode, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public static void displayNotification(Context context, Class targetClass, Notification notification) {
        displayNotification(context, targetClass, null, notification, null);
    }

    private static int getDefaults() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= NOTIFICATION_SOUND_START_HOUR && hour <= NOTIFICATION_SOUND_END_HOUR) {
            return NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_LIGHTS;
        }
        return NotificationCompat.DEFAULT_VIBRATE;
    }

    public static void updateNotificationIdIfNeeded(final Activity activity) {
        final String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        String notificationId = SharedPreferencesHelper.getStringPreference(Constants.KEY_NOTIFICATION_ID);
        if (!TextUtils.isEmpty(userId) && TextUtils.isEmpty(notificationId)) {
            FruityGcmClient.start(activity, Constants.GCM_SENDER_ID, new FruityGcmListener() {

                @Override
                public void onPlayServiceNotAvailable(boolean b) {
                }

                @Override
                public void onDeliverRegistrationId(final String regId, boolean b) {
                    SharedPreferencesHelper.setPreference(Constants.KEY_NOTIFICATION_ID, regId);
                    User user = new User();
                    user.setId(userId);
                    user.setNotificationId(regId);
                    ApiClient.getClient(activity).updateUser(user);

                }

                @Override
                public void onRegisterFailed() {
                }
            });
        }
    }
}
