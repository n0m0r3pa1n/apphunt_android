package com.apphunt.app.auth;

import android.app.Activity;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/18/15.
 * *
 * * NaughtySpirit 2015
 */
public class NullLoginProvider extends BaseLoginProvider {

    public static final String PROVIDER_NAME = "null";

    public NullLoginProvider(Activity activity) {
        super(activity);
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
}
