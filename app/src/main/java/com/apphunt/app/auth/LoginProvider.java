package com.apphunt.app.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.apphunt.app.MainActivity;
import com.apphunt.app.api.models.User;

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
