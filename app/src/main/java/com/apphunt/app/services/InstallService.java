package com.apphunt.app.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.apps.Packages;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.api.apphunt.models.notifications.NotificationType;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.db.models.InstalledApp;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.PackagesUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.ui.NotificationsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by nmp on 15-7-30.
 */
public class InstallService extends Service {
    public static final String TAG = InstallService.class.getSimpleName();

    private Realm realm;

    private List<ApplicationInfo> data;
    private RealmResults<InstalledApp> dailyInstalledApps;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferencesHelper.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(realmConfiguration);
        if (!SharedPreferencesHelper.getBooleanPreference(Constants.IS_INSTALL_NOTIFICATION_ENABLED, true)) {
            return START_FLAG_RETRY;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        Date today = new Date(calendar.getTimeInMillis());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

        Date tomorrow = new Date(calendar.getTimeInMillis());

        dailyInstalledApps = realm.where(InstalledApp.class)
                .beginGroup()
                    .equalTo("hasNotificationShowed", false)
                    .between("dateInstalled", today, tomorrow)
                .endGroup()
                .findAll();

        if (dailyInstalledApps.size() == 0) {
            return START_FLAG_RETRY;
        }

        data = PackagesUtils.getInstance().getInstalledPackages(getPackageManager());
        ApiClient.getClient(this).filterApps(getInstalledPackageNames(), new Response.Listener<Packages>() {
            @Override
            public void onResponse(Packages response) {
                displayNotificationForInstalledApp(response);
            }
        });

        return START_FLAG_RETRY;
    }



    public static void setupService(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, InstallService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, Constants.RC_INSTALL_SERVICE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmMgr == null) {
            Log.d(TAG, "AlarmMgr is null");
            return;
        }

        if (isInstallNotificationDisabled()) {
            alarmMgr.cancel(alarmIntent);
            return;
        }

        Calendar today = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (today.after(calendar)) {
            calendar.add(Calendar.DATE, +1);
        }

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @NonNull
    private static Boolean isInstallNotificationDisabled() {
        return !SharedPreferencesHelper.getBooleanPreference(Constants.IS_INSTALL_NOTIFICATION_ENABLED, true);
    }


    public void displayNotificationForInstalledApp(Packages event) {
        if (event.getAvailablePackages() != null &&
                event.getAvailablePackages().size() > 0) {

            InstalledApp app = findDailyInstalledAppInAvailableApps(event);

            if (app == null) {
                return;
            }

            FlurryWrapper.logEvent(TrackingEvents.UserViewedAddAppToAppHuntNotification);
            updateNotificationDisplayed(app);

            ApplicationInfo applicationInfo = PackagesUtils.getApplicationInfo(getPackageManager(), app.getPackageName());
            String appName = applicationInfo == null ? "" : applicationInfo.loadLabel(getPackageManager()).toString();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.EXTRA_APP_PACKAGE, app.getPackageName());
            NotificationsUtils.displayNotification(this, MainActivity.class,
                    bundle,
                    new Notification("Do you like " + appName + "?",
                            "Share it with the AppHunt community!", "", NotificationType.INSTALL),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        }
    }

    @Nullable
    private InstalledApp findDailyInstalledAppInAvailableApps(Packages event) {
        InstalledApp app = null;
        List<ApplicationInfo> availablePackages = getAvailablePackages(event);
        for (ApplicationInfo availableAppInfo : availablePackages) {
            for (InstalledApp dailyApp : dailyInstalledApps) {
                if (dailyApp.getPackageName().equals(availableAppInfo.packageName)) {
                    app = dailyApp;
                    break;
                }
            }
            if (app != null) {
                break;
            }
        }
        return app;
    }

    private List<ApplicationInfo> getAvailablePackages(Packages packages) {
        List<ApplicationInfo> tempData = new ArrayList<>();

        for (ApplicationInfo info : data) {
            for (String packageName : packages.getAvailablePackages()) {
                if (info.packageName.equals(packageName)) {
                    tempData.add(info);
                }
            }
        }

        return tempData;
    }

    private void updateNotificationDisplayed(InstalledApp app) {
        realm.beginTransaction();
        InstalledApp installedApp = realm.where(InstalledApp.class)
                .equalTo("packageName", app.getPackageName())
                .findFirst();
        installedApp.setHasNotificationShowed(true);
        realm.commitTransaction();
    }



    private Packages getInstalledPackageNames() {
        Packages packages = new Packages();
        for (ApplicationInfo info : data) {
            packages.getPackages().add(info.packageName);
        }

        return packages;
    }
}
