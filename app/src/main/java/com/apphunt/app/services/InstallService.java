package com.apphunt.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.db.models.ClickedApp;
import com.apphunt.app.db.models.InstalledApp;
import com.apphunt.app.utils.PackagesUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nmp on 15-7-30.
 */
public class InstallService extends IntentService {
    public static final String TAG = InstallService.class.getSimpleName();
    public static final int THIRTY_MINS = 30 * 60 * 1000;

    private Realm realm;
    private RealmResults<ClickedApp> clickedApps;

    public InstallService() {
        super("InstallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        realm = Realm.getInstance(this);
        long fromDate = System.currentTimeMillis() - THIRTY_MINS;

        RealmResults<InstalledApp> installedApps = realm.where(InstalledApp.class)
                .between("dateInstalled", new Date(fromDate), new Date())
                .findAll();

        clickedApps = realm.where(ClickedApp.class)
                .between("dateClicked", new Date(fromDate), new Date())
                .findAll();

        List<InstalledApp> recentlyInstalledApps = new ArrayList<>();
        for (int i = 0; i < installedApps.size(); i++) {
            InstalledApp app = installedApps.get(i);
            //if(wasOpenedFromAppHunt(app)) {
                recentlyInstalledApps.add(app);
            //}
        }

//        if(recentlyInstalledApps.size() == 0) {
//            return;
//        }

        InstalledApp app = recentlyInstalledApps.get(
                getRandomAppPosition(recentlyInstalledApps.size())
        );
        ApplicationInfo applicationInfo = PackagesUtils.getApplicationInfo(getPackageManager(), app.getPackageName());
        String appName = applicationInfo == null ? "" : applicationInfo.loadLabel(getPackageManager()).toString();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_APP_PACKAGE, app.getPackageName());
        NotificationsUtils.displayNotification(this, MainActivity.class,
                bundle,
                new Notification("Pssst",
                        "Did you like " + appName, "", ""),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_small_notification));
    }

    private int getRandomAppPosition(int recentlyInstalledAppsSize) {
        if(recentlyInstalledAppsSize <= 0)
            return 0;

        Random random = new Random();
        return random.nextInt(recentlyInstalledAppsSize);
    }

    private boolean wasOpenedFromAppHunt(InstalledApp app) {
        if (clickedApps == null || clickedApps.size() == 0) {
            return false;
        }

        for (int i = 0; i < clickedApps.size(); i++) {
            if(clickedApps.get(i).getPackageName().equals(app.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
