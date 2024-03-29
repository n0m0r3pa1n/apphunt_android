package com.apphunt.app.auth;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.models.Friend;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.tracker.EventTracker;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.SharedPreferencesHelper;

import java.util.ArrayList;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public abstract class BaseLoginProvider implements LoginProvider {
    public static final String TAG = BaseLoginProvider.class.getSimpleName();
    protected Context context;

    public interface OnFriendsResultListener {
        void onFriendsReceived(ArrayList<Friend> friends);
    }

    public BaseLoginProvider(Context context) {
        this.context = context;
    }

    @Override
    public void logout() {
        removeSharedPreferences();
        BusProvider.getInstance().post(new LogoutEvent());
        EventTracker.getInstance().resetActions();
    }

    @Override
    public void login(User user) {
        user.setLoginType(this.getLoginType());
        ApiClient.getClient(context).createUser(user, new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {
                onUserCreated(response);
                BusProvider.getInstance().post(new LoginEvent(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });
    }

    @Override
    public User getUser() {
        User user = new User();
        user.setId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
        user.setLoginType(SharedPreferencesHelper.getStringPreference(Constants.KEY_LOGIN_TYPE, ""));
        user.setEmail(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_EMAIL));
        user.setProfilePicture(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_PROFILE_PICTURE));
        user.setName(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_NAME));
        user.setUsername(SharedPreferencesHelper.getStringPreference(Constants.KEY_USERNAME));
        user.setCoverPicture(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_COVER_PICTURE));

        return user;
    }

    @Override
    public boolean isUserLoggedIn() {
        String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        String loginProvider = SharedPreferencesHelper.getStringPreference(Constants.KEY_LOGIN_PROVIDER_CLASS);
        return !TextUtils.isEmpty(loginProvider) && !TextUtils.isEmpty(userId);
    }

    private void onUserCreated(User user) {
        FlurryWrapper.logEvent(TrackingEvents.UserLoggedIn);
        saveSharedPreferences(user);
    }

    protected void saveSharedPreferences(User user) {
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_ID, user.getId());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_EMAIL, user.getEmail());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_NAME, user.getName());
        SharedPreferencesHelper.setPreference(Constants.KEY_USERNAME, user.getUsername());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_PROFILE_PICTURE, user.getProfilePicture());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_COVER_PICTURE, user.getCoverPicture());
        SharedPreferencesHelper.setPreference(Constants.KEY_LOGIN_TYPE, user.getLoginType());
    }

    protected void removeSharedPreferences() {
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_ID);
        SharedPreferencesHelper.removePreference(Constants.KEY_USERNAME);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_NAME);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_EMAIL);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_PROFILE_PICTURE);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_COVER_PICTURE);
        SharedPreferencesHelper.removePreference(Constants.KEY_LOGIN_TYPE);
        SharedPreferencesHelper.removePreference(Constants.KEY_LOGIN_PROVIDER_CLASS);
    }

    @Override
    public void loadFriends(BaseLoginProvider.OnFriendsResultListener listener) {
    }

    protected Activity getActivity() {
        return (Activity) context;
    }
}
