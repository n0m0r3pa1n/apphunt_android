package com.apphunt.app.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.apphunt.app.MainActivity;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.api.apphunt.models.notifications.NotificationType;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.db.models.ClickedApp;
import com.apphunt.app.db.models.InstalledApp;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.PackagesUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by nmp on 15-11-17.
 */
public class CommentAppService extends Service {
    public static final String TAG = CommentAppService.class.getSimpleName();

    private boolean isRemoved = false;
    private String appPackage = "";

    private Realm realm;
    private RealmConfiguration realmConfiguration;

    private RealmResults<InstalledApp> appsInstalledToday;
    private RealmResults<ClickedApp> appsClickedToInstallToday;
    private List<InstalledApp> appsStillInstalled = new ArrayList<>();
    private List<InstalledApp> appsInstalledAndRemoved = new ArrayList<>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clearArrays();
        SharedPreferencesHelper.init(this);
        FlurryWrapper.init(this);
        realmConfiguration = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
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

        appsInstalledToday = realm.where(InstalledApp.class)
                .beginGroup()
                    .equalTo("hasNotificationShowed", false)
                    .between("dateInstalled", today, tomorrow)
                .endGroup()
                .findAll();

        appsClickedToInstallToday = realm.where(ClickedApp.class)
                .between("dateClicked", today, tomorrow)
                .findAll();

        if (appsInstalledToday.size() == 0 || appsClickedToInstallToday.size() == 0) {
            return START_FLAG_RETRY;
        }

        List<InstalledApp> appsFromAppHunt = getUserAppsFromAppHunt();

        if (appsFromAppHunt.size() == 0) {
            return START_FLAG_RETRY;
        }

        populateUserApps(appsFromAppHunt);
        Random random = new Random();
        InstalledApp app = null;

        if (appsStillInstalled.size() > 0) {
            app = appsStillInstalled.get(random.nextInt(appsStillInstalled.size()));
            isRemoved = false;
        } else if (appsInstalledAndRemoved.size() > 0) {
            app = appsInstalledAndRemoved.get(random.nextInt(appsInstalledAndRemoved.size()));
            isRemoved = true;
        }

        if(app == null) {
            return START_FLAG_RETRY;
        }

        ArrayList<String> packages = new ArrayList<>();
        packages.add(app.getPackageName());
        appPackage = app.getPackageName();
        ApiClient.getClient(this).getAppsForPackages(packages, new Response.Listener<JsonArray>() {
            @Override
            public void onResponse(JsonArray response) {
                if(response == null || response.size() ==0) {
                    return;
                }

                JsonObject app = response.get(0).getAsJsonObject();
                displayNotificationForInstalledApp(
                        app.get("name").getAsString(),
                        app.get("_id").getAsString(),
                        app.get("icon").getAsString());
            }
        });

        return START_FLAG_RETRY;
    }

    private void clearArrays() {
        if(appsInstalledAndRemoved != null && appsInstalledAndRemoved.size() > 0) {
            appsInstalledAndRemoved.clear();
        }

        if(appsStillInstalled != null && appsStillInstalled.size() > 0) {
            appsStillInstalled.clear();
        }

    }

    private List<InstalledApp> getUserAppsFromAppHunt() {
        List<InstalledApp> appHuntApps = new ArrayList<>();
        for (int i = 0; i < appsInstalledToday.size(); i++) {
            InstalledApp installedApp = appsInstalledToday.get(i);
            for (int j = 0; j < appsClickedToInstallToday.size(); j++) {
                ClickedApp clickedApp = appsClickedToInstallToday.get(j);
                if (installedApp.getPackageName().equals(clickedApp.getPackageName())) {
                    appHuntApps.add(installedApp);
                    break;
                }
            }
        }
        return appHuntApps;
    }

    public void populateUserApps(List<InstalledApp> appsInstalledToday) {
        for (int i = 0; i < appsInstalledToday.size(); i++) {
            InstalledApp app = appsInstalledToday.get(i);
            if (isAppStillInstalled(app)) {
                appsStillInstalled.add(app);
            } else {
                appsInstalledAndRemoved.add(app);
            }
        }
    }

    public boolean isAppStillInstalled(InstalledApp app) {
        List<ApplicationInfo> currentlyExistingApps = PackagesUtils.getInstance().getInstalledPackages(getPackageManager());
        for (int j = 0; j < currentlyExistingApps.size(); j++) {
            ApplicationInfo applicationInfo = currentlyExistingApps.get(j);
            if (app.getPackageName().equals(applicationInfo.packageName)) {
                return true;
            }
        }

        return false;
    }

    public static void setupService(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CommentAppService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, Constants.RC_COMMENT_APP_SERVICE, intent,
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

        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (today.after(calendar)) {
            calendar.add(Calendar.DATE, +1);
        }

//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(),
                1000, alarmIntent);
    }

    @NonNull
    private static Boolean isInstallNotificationDisabled() {
        return !SharedPreferencesHelper.getBooleanPreference(Constants.IS_INSTALL_NOTIFICATION_ENABLED, true);
    }


    public void displayNotificationForInstalledApp(String appName, String appId, final String appIcon) {
        FlurryWrapper.logEvent(TrackingEvents.UserViewedCommentAppNotification);
        updateNotificationDisplayed();

        final Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_APP_ID, appId);

        final String notificationTitle;
        final String notificationMessage;
        if(isRemoved) {
            notificationTitle = "Did you like " + appName + "?";
            notificationMessage = "Suggest some improvements to the developer in AppHunt!";
        } else {
            notificationTitle = "Do you like " + appName + "?";
            notificationMessage = "Leave a comment on AppHunt!";
        }

        new Thread() {
            @Override
            public void run() {
                Bitmap largeIcon = null;
                try {
                    if(!TextUtils.isEmpty(appIcon)) {
                        largeIcon = Picasso.with(CommentAppService.this).load(appIcon).get();
                    }
                } catch (IOException ex) {
                    Crashlytics.logException(ex);
                }

                NotificationsUtils.displayNotification(CommentAppService.this, MainActivity.class,
                        bundle,
                        new Notification(notificationTitle,
                                notificationMessage, "", NotificationType.COMMENT_APP),
                        largeIcon);

            }
        }.start();

    }

    private void updateNotificationDisplayed() {
        Log.d(TAG, appPackage);
        realm = Realm.getInstance(realmConfiguration);
        realm.beginTransaction();
        InstalledApp installedApp = realm.where(InstalledApp.class)
                .equalTo("packageName", appPackage)
                .findFirst();
        installedApp.setHasNotificationShowed(true);
        realm.commitTransaction();
    }
}

