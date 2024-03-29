package com.apphunt.app.auth;

import com.apphunt.app.api.apphunt.models.users.User;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public interface LoginProvider {
    void login(User user);
    void logout();
    void loadFriends(BaseLoginProvider.OnFriendsResultListener listener);
    boolean isUserLoggedIn();
    String getName();
    String getLoginType();
    User getUser();
}
