package com.apphunt.app;

import android.text.TextUtils;

import com.apphunt.app.api.apphunt.VolleyInstance;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.GsonInstance;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.branch.referral.BranchApp;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/6/15.
 */
public class AppHuntApplication extends BranchApp {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferencesHelper.init(this);
        initNetworking();
        initAnalytics();
        LoginProviderFactory.onCreate(getApplicationContext());
    }

    private void initAnalytics() {
        String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        if (!TextUtils.isEmpty(userId)) {
            FlurryWrapper.setUserId(userId);
        }

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(Constants.TWITTER_CONSUMER_KEY,
                        Constants.TWITTER_CONSUMER_SECRET);
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Twitter(authConfig), new Crashlytics.Builder().core(core).build());

        FacebookSdk.sdkInitialize(getApplicationContext(), Constants.FACEBOOK_SIGN_IN);

        if (BuildConfig.DEBUG) {
            FlurryWrapper.init(this, Constants.FLURRY_DEBUG_API_KEY);
        } else {
            FlurryWrapper.init(this, Constants.FLURRY_API_KEY);
        }
    }

    private void initNetworking() {
        VolleyInstance.getInstance(this);
        GsonInstance.init();
    }
}
