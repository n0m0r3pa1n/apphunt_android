package com.apphunt.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.apphunt.app.db.models.InstalledApp;

import java.util.Date;

import io.realm.Realm;

public class InstallReceiver extends BroadcastReceiver {
    public static final String TAG = InstallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri uri = intent.getData();
        String[] str = uri.toString().split(":");
        String packageInstalled = str[str.length-1];

        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        InstalledApp installedApp = realm.createObject(InstalledApp.class);
        installedApp.setPackageName(packageInstalled);
        installedApp.setDateInstalled(new Date());
        realm.commitTransaction();
    }
}
