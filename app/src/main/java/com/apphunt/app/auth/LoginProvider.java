package com.apphunt.app.auth;

import com.apphunt.app.api.apphunt.models.User;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public interface LoginProvider {
    void login(User user);

    void logout();

    boolean isUserLoggedIn();

    String getName();
}
