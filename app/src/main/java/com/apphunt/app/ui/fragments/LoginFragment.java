package com.apphunt.app.ui.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.api.twitter.models.Friends;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.ui.interfaces.UserLoginScreenListener;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoadersUtils;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.AccountPicker;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginFragment extends BaseFragment {

    private static final String TAG = LoginFragment.class.getName();

    private ActionBarActivity activity;

    private User user;
    private TwitterLoginButton loginButton;

    private boolean canBeSkipped = false;
    private UserLoginScreenListener userLoginScreenListener;

    public void setCanBeSkipped(boolean canBeSkipped) {
        this.canBeSkipped = canBeSkipped;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_login);
        FlurryAgent.logEvent(TrackingEvents.UserViewedLogin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button notNowButton = (Button) view.findViewById(R.id.not_now);
        notNowButton.setVisibility(View.GONE);
        if(canBeSkipped) {
            notNowButton.setVisibility(View.VISIBLE);
            notNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userLoginScreenListener.onLoginSkipped();
                }
            });
        }

        loginButton = (TwitterLoginButton) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginProviderFactory.get(activity).isUserLoggedIn()) {
                    LoadersUtils.showBottomLoader(activity, R.drawable.loader_white, false);
                    ((MainActivity) activity).setOnBackBlocked(true);
                }
            }
        });
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                final AppHuntTwitterApiClient appHuntTwitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
                boolean doNotIncludeEntities = false;
                boolean skipStatus = true;
                appHuntTwitterApiClient.getAccountService().verifyCredentials(doNotIncludeEntities, skipStatus, new Callback<com.twitter.sdk.android.core.models.User>() {

                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                        com.twitter.sdk.android.core.models.User twitterUser = userResult.data;
                        user = new User();
                        user.setUsername(twitterUser.screenName);
                        user.setName(twitterUser.name);
                        user.setProfilePicture(twitterUser.profileImageUrl);
                        user.setLoginType(TwitterLoginProvider.PROVIDER_NAME);
                        Locale locale = getResources().getConfiguration().locale;
                        user.setLocale(String.format("%s-%s", locale.getCountry().toLowerCase(), locale.getLanguage()).toLowerCase());

                        appHuntTwitterApiClient.getFriendsService().getFriends(userResult.data.screenName, new Callback<Friends>() {
                            @Override
                            public void success(Result<Friends> friendsResult) {
                                List<String> following = new ArrayList<>();
                                for (com.twitter.sdk.android.core.models.User user :
                                        friendsResult.data.getUsers()) {
                                    following.add(user.screenName);
                                }
                                user.setFollowing(following);
                                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                                        false, null, null, null, null);
                                startActivityForResult(intent, Constants.REQUEST_ACCOUNT_EMAIL);
                            }

                            @Override
                            public void failure(TwitterException e) {
                                onLoginFailed();
                            }
                        });
                    }

                    @Override
                    public void failure(TwitterException e) {
                        onLoginFailed();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                onLoginFailed();
            }
        });

        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_out_top);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActionBarActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) activity).setOnBackBlocked(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        loginButton.onActivityResult(requestCode, resultCode,
                data);

        if (requestCode == Constants.REQUEST_ACCOUNT_EMAIL) {
            if (resultCode == Activity.RESULT_OK) {
                String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                user.setEmail(email);
                LoginProviderFactory.setLoginProvider(activity, new TwitterLoginProvider(activity));
                LoginProviderFactory.get(activity).login(user);
                userLoginScreenListener.onLoginSuccessful();
            } else {
                onLoginFailed();
                userLoginScreenListener.onLoginFailed();
            }
        }
    }

    private void onLoginFailed() {
        if (!isAdded()) {
            return;
        }
        Toast.makeText(activity, R.string.login_canceled_text, Toast.LENGTH_LONG).show();
        ((MainActivity) activity).setOnBackBlocked(false);
        LoadersUtils.hideBottomLoader(activity);
    }


    public void setUserLoginScreenListener(UserLoginScreenListener userLoginScreenListener) {
        this.userLoginScreenListener = userLoginScreenListener;
    }
}
