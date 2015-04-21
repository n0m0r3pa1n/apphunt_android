package com.apphunt.app;

import android.app.Application;
import android.text.TextUtils;

import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
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
        FlurryAgent.setLogEnabled(true);
        String userId = SharedPreferencesHelper.getStringPreference(this, Constants.KEY_USER_ID);
        if (!TextUtils.isEmpty(userId)) {
            FlurryAgent.setUserId(userId);
        }

        FlurryAgent.init(this, Constants.FLURRY_API_KEY);

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("XCYebUTguuAJdTrF56zksVtmZ",
                        "EmN3llE9vdhD3wRX1Z7E15rYVVqYbUYwx0uSDAd2yNPShLuM7x");
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
    }
}
