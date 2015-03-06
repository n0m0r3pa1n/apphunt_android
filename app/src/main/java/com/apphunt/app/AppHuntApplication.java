package com.apphunt.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/6/15.
 */
public class AppHuntApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("XCYebUTguuAJdTrF56zksVtmZ",
                        "EmN3llE9vdhD3wRX1Z7E15rYVVqYbUYwx0uSDAd2yNPShLuM7x");
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
    }
}
