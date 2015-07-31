package com.apphunt.app.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.db.models.ClickedApp;
import com.apphunt.app.db.models.InstalledApp;
import com.apphunt.app.utils.PackagesUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.ui.NotificationsUtils;

import java.util.ArrayList;
import java.util.Calendar;
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


    private Realm realm;
    private RealmResults<ClickedApp> clickedApps;

    public InstallService() {
        super("InstallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferencesHelper.init(this);

        if(!SharedPreferencesHelper.getBooleanPreference(Constants.IS_INSTALL_NOTIFICATION_ENABLED)) {
            return;
        }

        realm = Realm.getInstance(this);
        long fromDate = System.currentTimeMillis() - Constants.THIRTY_MINS;

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

        if(recentlyInstalledApps.size() == 0) {
            return;
        }

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
                        "Did you like " + appName + "? Share your opinion with others!", "", ""),
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

    public static void setupService(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, InstallService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(alarmMgr == null) {
            Log.d(TAG, "AlarmMgr is null");
            return;
        }

        if(isInstallNotificationDisabled()) {
            alarmMgr.cancel(alarmIntent);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30*1000
                , alarmIntent);
    }

    @NonNull
    private static Boolean isInstallNotificationDisabled() {
        return !SharedPreferencesHelper.getBooleanPreference(Constants.IS_INSTALL_NOTIFICATION_ENABLED, true);
    }
}
