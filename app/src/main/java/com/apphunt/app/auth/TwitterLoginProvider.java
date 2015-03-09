package com.apphunt.app.auth;

import android.app.Activity;

import com.twitter.sdk.android.Twitter;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/6/15.
 */
public class TwitterLoginProvider extends BaseLoginProvider {
    public static final String PROVIDER_NAME = "twitter";

    public TwitterLoginProvider(Activity activity) {
        super(activity);
    }

    @Override
    public void logout() {
        Twitter.logOut();
        super.logout();
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
}
