package com.apphunt.app.ui.fragments.notification;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.api.twitter.models.Friends;
import com.apphunt.app.auth.GooglePlusLoginProvider;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserCreatedApiEvent;
import com.apphunt.app.event_bus.events.ui.HideFragmentEvent;
import com.apphunt.app.event_bus.events.ui.LoginSkippedEvent;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
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
import butterknife.OnClick;

public class LoginFragment extends BaseFragment implements OnConnectionFailedListener, ConnectionCallbacks {

    private static final String TAG = LoginFragment.class.getName();

    @InjectView(R.id.login_message)
    TextView loginMessage;

    @InjectView(R.id.twitter_login_button)
    TwitterLoginButton twitterLoginBtn;

    @InjectView(R.id.gplus_login_button)
    SignInButton gplusSignInBtn;

    private ActionBarActivity activity;
    private User user;

    private boolean canBeSkipped = false;
    private boolean isTwitterLogin = false;
    private String message;

    private GoogleApiClient googleApiClient;
    private Locale locale;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent(TrackingEvents.UserViewedLogin);

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        locale = getResources().getConfiguration().locale;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBarUtils.getInstance().hideActionBarShadow();

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        loginMessage.setText(message);

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

        twitterLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginProviderFactory.get(activity).isUserLoggedIn()) {
                    LoadersUtils.showBottomLoader(activity, R.drawable.loader_white, false);
                    NavUtils.getInstance(activity).setOnBackBlocked(true);
                }
            }
        });
        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> twitterSessionResult) {
                isTwitterLogin = true;
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

    @OnClick(R.id.gplus_login_button)
    public void onGPlusLoginBtnClick() {
        googleApiClient.connect();
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
    }

    @Override
    public void onStart() {
        super.onStart();
//        googleApiClient.connect();
    }

    public void setCanBeSkipped(boolean canBeSkipped) {
        this.canBeSkipped = canBeSkipped;
    }

    @Override
    public int getTitle() {
        return R.string.title_login;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NavUtils.getInstance(activity).setOnBackBlocked(false);
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (isTwitterLogin) {
            twitterLoginBtn.onActivityResult(requestCode, resultCode,
                    data);
        }

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
        } else if (requestCode == Constants.GPLUS_SIGN_IN) {
            googleApiClient.connect();
        }

        isTwitterLogin = false;
    }

    @Subscribe
    public void onUserCreated(UserCreatedApiEvent event) {
        Log.e(TAG, event.getUser().toString());
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


    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(int messageRes) {
        setMessage(getString(messageRes));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);

            User user = new User();
            user.setEmail(Plus.AccountApi.getAccountName(googleApiClient));
            user.setProfilePicture(currentPerson.getImage().getUrl());
            user.setCoverPicture(currentPerson.hasCover() ? currentPerson.getCover().getCoverPhoto().getUrl() : null);
            user.setName(currentPerson.getName().getGivenName() + " " + currentPerson.getName().getFamilyName());
            user.setUsername(currentPerson.hasNickname() ? currentPerson.getNickname() : currentPerson.getDisplayName());
            user.setLocale(String.format("%s-%s", locale.getCountry().toLowerCase(), locale.getLanguage()).toLowerCase());
            user.setLoginType(GooglePlusLoginProvider.PROVIDER_NAME);

            LoginProviderFactory.setLoginProvider(activity, new GooglePlusLoginProvider(activity));
            ApiClient.getClient(getActivity()).createUser(user);
            LoadersUtils.showBottomLoader(activity, R.drawable.loader_white, false);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(activity, "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.toString());

        try {
            connectionResult.startResolutionForResult(activity, Constants.GPLUS_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
