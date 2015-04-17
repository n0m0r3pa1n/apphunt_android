package com.apphunt.app.api.twitter;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/6/15.
 */
public class AppHuntTwitterApiClient extends TwitterApiClient {
    public AppHuntTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public FriendsService getFriendsService() {
        return getService(FriendsService.class);
    }
}
