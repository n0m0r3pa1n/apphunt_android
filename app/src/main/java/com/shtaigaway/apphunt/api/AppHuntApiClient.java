package com.shtaigaway.apphunt.api;

import retrofit.RestAdapter;


public class AppHuntApiClient {
    private static AppHuntApi client;
    public static AppHuntApi getClient() {
        if(client == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://apphunt.herokuapp.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            client = restAdapter.create(AppHuntApi.class);
        }

        return client;
    }
}
