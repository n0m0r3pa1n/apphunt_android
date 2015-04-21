package com.apphunt.app.ui.interfaces;


import android.content.pm.ApplicationInfo;

public interface UserLoginScreenListener {
    void onLoginSuccessful();

    void onLoginFailed();

    void onLoginSkipped();
}
