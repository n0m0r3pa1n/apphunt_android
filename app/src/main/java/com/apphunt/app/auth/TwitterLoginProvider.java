package com.apphunt.app.auth;

import android.content.Context;

import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.api.twitter.models.Friends;
import com.apphunt.app.auth.models.Friend;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/6/15.
 */
public class TwitterLoginProvider extends BaseLoginProvider {
    public static final String PROVIDER_NAME = "twitter";

    public TwitterLoginProvider(Context context) {
        super(context);
    }

    @Override
    public void loadFriends(final OnFriendsResultListener listener) {
        final AppHuntTwitterApiClient appHuntTwitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
        boolean doNotIncludeEntities = false;
        boolean skipStatus = true;
        appHuntTwitterApiClient.getAccountService().verifyCredentials(doNotIncludeEntities, skipStatus, new Callback<User>() {

            @Override
            public void success(Result<User> userResult) {
                appHuntTwitterApiClient.getFriendsService().getFriends(userResult.data.screenName, new Callback<Friends>() {
                    @Override
                    public void success(Result<Friends> friendsResult) {
                        ArrayList<Friend> following = new ArrayList<>();
                        for (com.twitter.sdk.android.core.models.User user :
                                friendsResult.data.getUsers()) {

                            Friend friend = new Friend();
                            friend.setId(String.valueOf(user.getId()));
                            friend.setName(user.name);
                            friend.setUsername(user.screenName);
                            friend.setProfileImage(user.profileImageUrl.replace("_normal", ""));

                            following.add(friend);
                        }
                        listener.onFriendsReceived(following);
                    }

                    @Override
                    public void failure(TwitterException e) {
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
            }
        });
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
