package com.apphunt.app.auth;

import android.content.Context;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/31/15.
 * *
 * * NaughtySpirit 2015
 */
public class GooglePlusLoginProvider extends BaseLoginProvider {
    public static final String PROVIDER_NAME = "google-plus";

    private GoogleApiClient googleApiClient;

    public GooglePlusLoginProvider(Context context) {
        super(context);
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
        googleApiClient.connect();
    }

    @Override
    public void logout() {
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
        }
        super.logout();
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
}
