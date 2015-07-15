package com.apphunt.app.api.apphunt.client;

import android.content.Context;

public class ApiClient {
    private static AppHuntApiClient client;

    public static AppHuntApi getClient(Context context) {
        if (client == null) {
            client = new AppHuntApiClient(context);
        }

        return client;
    }
}
