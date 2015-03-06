package com.apphunt.app.api.apphunt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;


public class AppHuntApiClient {
    private static AppHuntApi client;
    private static ExecutorService executorService;

    public static AppHuntApi getClient() {
        if(client == null) {
            executorService = Executors.newCachedThreadPool();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://apphunt.herokuapp.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setExecutors(executorService, new MainThreadExecutor())
                    .build();

            client = restAdapter.create(AppHuntApi.class);
        }

        return client;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
