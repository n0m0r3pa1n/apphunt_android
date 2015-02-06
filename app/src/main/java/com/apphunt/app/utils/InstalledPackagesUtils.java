package com.apphunt.app.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class InstalledPackagesUtils {
    private static final String TAG =  InstalledPackagesUtils.class.getName();

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

}