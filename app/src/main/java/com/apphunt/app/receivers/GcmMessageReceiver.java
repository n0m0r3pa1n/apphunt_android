package com.apphunt.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.apphunt.app.MainActivity;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.api.apphunt.models.notifications.NotificationType;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 4/20/15.
 */
public class GcmMessageReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_KEY_TITLE = "title";
    public static final String NOTIFICATION_KEY_MESSAGE = "message";
    public static final String NOTIFICATION_KEY_IMAGE = "image";
    public static final String NOTIFICATION_KEY_TYPE = "type";
    public static final String APP_ID = "appId";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        SharedPreferencesHelper.init(context);
        final Bundle e = intent.getExtras();

        if (SharedPreferencesHelper.getBooleanPreference(Constants.SETTING_NOTIFICATIONS_ENABLED, true)) {
            final Notification notification = new Notification(e.getString(NOTIFICATION_KEY_TITLE), e.getString(NOTIFICATION_KEY_MESSAGE),
                    e.getString(NOTIFICATION_KEY_IMAGE), NotificationType.getType(e.getString(NOTIFICATION_KEY_TYPE)));
            new Thread() {
                @Override
                public void run() {

                    super.run();
                    Bitmap largeIcon = null;
                    try {
                        largeIcon = Picasso.with(context).load(e.getString(NOTIFICATION_KEY_IMAGE)).get();
                    } catch (IOException ex) {
                        Crashlytics.logException(ex);
                    }

                    Bundle bundle = null;

                    if (e.containsKey("data")) {
                        try {
                            JSONObject json = new JSONObject(e.getString("data"));
                            if (json.has(APP_ID)) {
                                bundle = new Bundle();
                                bundle.putString(Constants.KEY_NOTIFICATION_APP_ID, json.getString(APP_ID));
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    NotificationsUtils.displayNotification(context, MainActivity.class, bundle, notification, largeIcon);
                    Map<String, String> params = new HashMap<>();
                    params.put("type", notification.getType().toString());
                    FlurryAgent.logEvent(TrackingEvents.AppShowedNotification, params);
                }
            }.start();
        }
    }
}
