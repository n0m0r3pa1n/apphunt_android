package com.apphunt.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 4/20/15.
 */
public class GcmMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        if (SharedPreferencesHelper.getBooleanPreference(context, Constants.IS_DAILY_NOTIFICATION_ENABLED)) {

        }


        for (String s : extras.keySet()) {
            Log.d("GcmMessageReceiver", "GcmMessageReceiver > onReceive : " + s + " = " + extras.get(s));
        }
    }
}
