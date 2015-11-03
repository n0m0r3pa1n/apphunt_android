package com.apphunt.app.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 11/3/15.
 * *
 * * NaughtySpirit 2015
 */
public class GoogleUtils {

    public interface OnResultListener {
        void onResultReady(String advertisingId);
    }

    public static void obtainAdvertisingId(final Context context, final OnResultListener listener) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                AdvertisingIdClient.Info idInfo = null;
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String advertId = null;

                try{
                    advertId = idInfo.getId();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

                return advertId;
            }

            @Override
            protected void onPostExecute(String advertId) {
                listener.onResultReady(advertId);
            }

        };
        task.execute();
    }
}
