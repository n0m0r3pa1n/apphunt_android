package com.apphunt.app.auth;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.models.Friend;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.fragments.invites.InvitesFragment;
import com.apphunt.app.ui.fragments.login.LoginFragment;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public abstract class BaseLoginProvider implements LoginProvider {

    private final Activity activity;

    public interface OnFriendsResultListener {
        void onFriendsReceived(ArrayList<Friend> friends);
    }

    public BaseLoginProvider(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void logout() {
        removeSharedPreferences();
        BusProvider.getInstance().post(new LogoutEvent());
        hideLoginFragment(activity);
    }

    @Override
    public void login(User user) {
        onUserCreated(user);
        BusProvider.getInstance().post(new LoginEvent(user));
    }

    @Override
    public User getUser() {
        User user = new User();
        user.setId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
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
        String loginProvider = SharedPreferencesHelper.getStringPreference(Constants.KEY_LOGIN_PROVIDER);
        return !TextUtils.isEmpty(loginProvider) && !TextUtils.isEmpty(userId);
    }

    private void onUserCreated(User user) {
        FlurryAgent.logEvent(TrackingEvents.UserLoggedIn);
        saveSharedPreferences(user);
//        hideLoginFragment(activity);
        presentInvitesScreen();
    }

    protected void saveSharedPreferences(User user) {
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_ID, user.getId());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_EMAIL, user.getEmail());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_NAME, user.getName());
        SharedPreferencesHelper.setPreference(Constants.KEY_USERNAME, user.getUsername());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_PROFILE_PICTURE, user.getProfilePicture());
        SharedPreferencesHelper.setPreference(Constants.KEY_USER_COVER_PICTURE, user.getCoverPicture());
        SharedPreferencesHelper.setPreference(Constants.KEY_LOGIN_PROVIDER, user.getLoginType());
    }

    protected void removeSharedPreferences() {
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_ID);
        SharedPreferencesHelper.removePreference(Constants.KEY_USERNAME);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_NAME);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_EMAIL);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_PROFILE_PICTURE);
        SharedPreferencesHelper.removePreference(Constants.KEY_USER_COVER_PICTURE);
        SharedPreferencesHelper.removePreference(Constants.KEY_LOGIN_PROVIDER);
    }

    private void hideLoginFragment(Context ctx) {
        FragmentManager fragmentManager = ((AppCompatActivity) ctx).getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(Constants.TAG_LOGIN_FRAGMENT);

        if (loginFragment != null) {
            fragmentManager.popBackStack();
        }
    }

    private void presentInvitesScreen() {
        ((AppCompatActivity) activity).getSupportFragmentManager().popBackStack();
        ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new InvitesFragment(), Constants.TAG_INVITE_FRAGMENT)
                .addToBackStack(Constants.TAG_INVITE_FRAGMENT)
                .commit();
    }

    @Override
    public void loadFriends(BaseLoginProvider.OnFriendsResultListener listener) {
    }

    protected Activity getActivity() {
        return activity;
    }
}
