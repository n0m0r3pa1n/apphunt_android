package com.apphunt.app.auth;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.FacebookUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.facebook.Session;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public class CustomLoginProvider extends BaseLoginProvider implements LoginProvider {
    public static final String PROVIDER_NAME = "custom";

    public CustomLoginProvider(Activity activity) {
        super(activity);
    }

    @Override
    public boolean isUserLoggedIn() {
        String userId = SharedPreferencesHelper.getStringPreference(getActivity(), Constants.KEY_USER_ID);
        String loginProvider = SharedPreferencesHelper.getStringPreference(getActivity(), Constants.KEY_LOGIN_PROVIDER);
        
        boolean isLoggedIn = !TextUtils.isEmpty(loginProvider) && !TextUtils.isEmpty(userId);
        
        if (!isLoggedIn && Session.getActiveSession().isOpened()) {
            Log.i("TAG", "called");
            Session.getActiveSession().close();
        }

        Log.i("TAG", userId + " " + loginProvider + " " + isLoggedIn);

        return isLoggedIn;
    }

    @Override
    public void onCreate(Activity activity, Bundle savedStateInstance) {

    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

}
