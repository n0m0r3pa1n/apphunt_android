package com.apphunt.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.apphunt.app.constants.Constants;

import java.util.ArrayList;
import java.util.List;


public class PackagesUtils {
    private static final String TAG = PackagesUtils.class.getName();

    public static List<ApplicationInfo> installedPackages(PackageManager packageManager) {
        List<ApplicationInfo> installedPackages = new ArrayList<ApplicationInfo>();

        try {
            List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo applicationInfo : packages) {
                if (!isSystemPackage(applicationInfo)) {
                    if (!applicationInfo.packageName.equals(Constants.PACKAGE_NAME))
                        installedPackages.add(applicationInfo);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return installedPackages;
    }

    private static boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void openInMarket(Context context, String appPackage) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appPackage));
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            marketIntent.setData(Uri.parse("market://details?id=" + appPackage));
            context.startActivity(marketIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            marketIntent.setData(Uri.parse(appPackage));
            context.startActivity(marketIntent);
        }
    }
}