package it.appspice.android.api;

import retrofit.RestAdapter;

/**
 * Created by nmp on 1/14/15.
 */
public class AppSpiceApiClient {
    private static AppSpiceApi client;
    public static AppSpiceApi getClient() {
        if(client == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://appspice-api.herokuapp.com/")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            client = restAdapter.create(AppSpiceApi.class);
        }

        return client;
    }
}
