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

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.api.twitter.models.Friends;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserCreatedApiEvent;
import com.apphunt.app.event_bus.events.ui.HideFragmentEvent;
import com.apphunt.app.event_bus.events.ui.LoginSkippedEvent;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.AccountPicker;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginFragment extends BaseFragment {

    private static final String TAG = LoginFragment.class.getName();

    private ActionBarActivity activity;

    private User user;

    @InjectView(R.id.login_button)
    TwitterLoginButton loginButton;

    private boolean canBeSkipped = false;

    public void setCanBeSkipped(boolean canBeSkipped) {
        this.canBeSkipped = canBeSkipped;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent(TrackingEvents.UserViewedLogin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBarUtils.getInstance().hideActionBarShadow();

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        Button notNowButton = (Button) view.findViewById(R.id.not_now);
        notNowButton.setVisibility(View.GONE);
        if(canBeSkipped) {
            notNowButton.setVisibility(View.VISIBLE);
            notNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BusProvider.getInstance().post(new LoginSkippedEvent());
                    BusProvider.getInstance().post(new HideFragmentEvent(Constants.TAG_LOGIN_FRAGMENT));
                }
            });
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginProviderFactory.get(activity).isUserLoggedIn()) {
                    LoadersUtils.showBottomLoader(activity, R.drawable.loader_white, false);
                    NavUtils.getInstance(activity).setOnBackBlocked(true);
                }
            }
        });
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> twitterSessionResult) {
                final AppHuntTwitterApiClient appHuntTwitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
                boolean doNotIncludeEntities = false;
                boolean skipStatus = true;
                appHuntTwitterApiClient.getAccountService().verifyCredentials(doNotIncludeEntities, skipStatus, new Callback<com.twitter.sdk.android.core.models.User>() {

                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                        final com.twitter.sdk.android.core.models.User twitterUser = userResult.data;
                        user = new User();
                        user.setUsername(twitterUser.screenName);
                        user.setName(twitterUser.name);

                        String profileImageUrl = twitterUser.profileImageUrl.replace("_normal", "");
                        user.setProfilePicture(profileImageUrl);
                        user.setLoginType(TwitterLoginProvider.PROVIDER_NAME);
                        Locale locale = getResources().getConfiguration().locale;
                        user.setLocale(String.format("%s-%s", locale.getCountry().toLowerCase(), locale.getLanguage()).toLowerCase());
                        user.setCoverPicture(twitterUser.profileBannerUrl != null ? twitterUser.profileBannerUrl : twitterUser.profileBackgroundImageUrl);

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
        BusProvider.getInstance().register(this);
        ActionBarUtils.getInstance().setTitle(R.string.title_login);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NavUtils.getInstance(activity).setOnBackBlocked(false);
        BusProvider.getInstance().unregister(this);
        ActionBarUtils.getInstance().setPreviousTitle();


        if (!ActionBarUtils.getInstance().getPreviousTitle().equals(getString(R.string.title_app_details)) &&
                !ActionBarUtils.getInstance().getPreviousTitle().equals(getString(R.string.title_save_app))) {
            ActionBarUtils.getInstance().showActionBarShadow();
        }
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
                ApiClient.getClient(getActivity()).createUser(user);
                LoadersUtils.showBottomLoader(activity, R.drawable.loader_white, false);
            } else {
                onLoginFailed();
            }
        }
    }

    @Subscribe
    public void onUserCreated(UserCreatedApiEvent event) {
        LoadersUtils.hideBottomLoader(activity);
        LoginProviderFactory.get(activity).login(event.getUser());
    }

    private void onLoginFailed() {
        if (!isAdded()) {
            return;
        }
        Toast.makeText(activity, R.string.login_canceled_text, Toast.LENGTH_LONG).show();
        NavUtils.getInstance(activity).setOnBackBlocked(false);
        LoadersUtils.hideBottomLoader(activity);
    }


}
