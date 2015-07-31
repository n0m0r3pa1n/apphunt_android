package com.apphunt.app.auth;

import android.app.Activity;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/31/15.
 * *
 * * NaughtySpirit 2015
 */
public class GooglePlusLoginProvider extends BaseLoginProvider {

    public static final String PROVIDER_NAME = "google-plus";

    public GooglePlusLoginProvider(Activity activity) {
        super(activity);
    }

    @Override
    public String getName() {
        return null;
    }
}
