package com.shtaigaway.apphunt.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by nmp on 12/23/14.
 */
public class ConnectivityUtils {

    /**
     * Checks for Internet connection.
     *
     * @param context a {@link android.content.Context}
     * @return true if the device or emulator is connected to the Internet;
     *         false otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
