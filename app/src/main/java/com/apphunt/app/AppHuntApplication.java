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
        String userId = SharedPreferencesHelper.getStringPreference(this, Constants.KEY_USER_ID);
        if (!TextUtils.isEmpty(userId)) {
            FlurryAgent.setUserId(userId);
        }

        FlurryAgent.init(this, Constants.FLURRY_API_KEY);

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("2GwWIq8PXArLO1YKieGNsAKQa",
                        "GG81rZvwLnFdxzSdtASsQMDaWZVr7bzzqRKBCWgnWCmpQqx5VK");
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
    }
}
