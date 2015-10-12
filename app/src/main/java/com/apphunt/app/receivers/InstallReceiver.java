package com.apphunt.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.apphunt.app.constants.Constants;
import com.apphunt.app.db.models.InstalledApp;
import com.apphunt.app.utils.SharedPreferencesHelper;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class InstallReceiver extends BroadcastReceiver {
    public static final String TAG = InstallReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferencesHelper.init(context);
        Uri uri = intent.getData();
        String[] str = uri.toString().split(":");
        String packageInstalled = str[str.length-1];

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).deleteRealmIfMigrationNeeded().build();
        Realm realm = Realm.getInstance(realmConfiguration);
        InstalledApp installedApp = realm.where(InstalledApp.class).equalTo("packageName", packageInstalled).findFirst();
        realm.beginTransaction();

        if (installedApp != null) {
            installedApp.setDateInstalled(new Date());
        } else {
            installedApp = realm.createObject(InstalledApp.class);
            installedApp.setPackageName(packageInstalled);
            installedApp.setDateInstalled(new Date());
        }

        realm.commitTransaction();
    }
}
