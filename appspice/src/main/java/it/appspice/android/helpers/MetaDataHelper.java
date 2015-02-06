package it.appspice.android.helpers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by Naughty Spirit
 * on 1/28/15.
 */

public class MetaDataHelper {

    public static final String TAG = "helpers.MetaDataHelper";

    public static String getMetaData(Context ctx, String name) {
        try {
            ApplicationInfo appInfo = ctx.getPackageManager().getApplicationInfo(
                    ctx.getPackageName(), PackageManager.GET_META_DATA
            );

            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, String.format("Cannot find MetaData with name %s", name));
        }

        return "";
    }
}
