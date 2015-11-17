package com.apphunt.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.PackagesUtils;

import java.util.HashMap;

/**
 * Created by nmp on 15-11-17.
 */
public class DeleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FlurryWrapper.init(context);
        PackagesUtils.getInstance().resetInstalledPackages();
        Uri uri = intent.getData();
        String[] str = uri.toString().split(":");
        final String packageRemoved = str[str.length-1];
        FlurryWrapper.logEvent(TrackingEvents.UserRemovedApp, new HashMap<String, String>() {{
            put("appPackage", packageRemoved);
        }});
    }
}
