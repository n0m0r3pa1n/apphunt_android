package com.apphunt.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by nmp on 15-7-30.
 */
public class InstallReceiver extends BroadcastReceiver {
    public static final String TAG = InstallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Uri uri = intent.getData();
        String packageInstalled = uri.getQueryParameter("package");
        Log.d(TAG, "onReceive " + packageInstalled);

    }
}
