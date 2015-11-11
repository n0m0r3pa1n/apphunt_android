package com.apphunt.app.ui.fragments.login;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.auth.FacebookLoginProvider;
import com.apphunt.app.auth.GooglePlusLoginProvider;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserCreatedApiEvent;
import com.apphunt.app.event_bus.events.ui.HideFragmentEvent;
import com.apphunt.app.event_bus.events.ui.LoginSkippedEvent;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.views.widgets.CustomFacebookButton;
import com.apphunt.app.ui.views.widgets.CustomGooglePlusButton;
import com.apphunt.app.ui.views.widgets.CustomTwitterLoginButton;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginFragment extends BackStackFragment implements OnConnectionFailedListener, ConnectionCallbacks {

    private static final String TAG = LoginFragment.class.getName();

    @InjectView(R.id.login_message)
    TextView loginMessage;

    @InjectView(R.id.login_button)
    CustomTwitterLoginButton twitterLoginBtn;

    @InjectView(R.id.gplus_login_button)
    CustomGooglePlusButton gplusSignInBtn;

    @InjectView(R.id.fb_login_button)
    CustomFacebookButton fbLoginBtn;

    private AppCompatActivity activity;
    private User user;

    private boolean canBeSkipped = false;
    private boolean isTwitterLogin = false;
    private boolean isFacebookLogin = false;

    private String message;
    private GoogleApiClient googleApiClient;

    private Locale locale;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryWrapper.logEvent(TrackingEvents.UserViewedLogin);

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        callbackManager = CallbackManager.Factory.create();

        locale = getResources().getConfiguration().locale;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBarUtils.getInstance().hideActionBarShadow();

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        loginMessage.setText(message);
        fbLoginBtn.setFragment(this);
        fbLoginBtn.setReadPermissions(Arrays.asList("email"));
        fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code

                                JSONObject json = response.getJSONObject();
                                final User user = new User();
                                String id = "";
                                try {
                                    id = json.getString("id");
                                    String email = json.getString("email");
                                    user.setEmail(email);
                                    user.setName(json.getString("name"));
                                    user.setUsername(email.split("@")[0]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (TextUtils.isEmpty(id)) {
                                    return;
                                }
                                setUserFbProfiledDataAndLogin(user, id);
                            }
                        }).executeAsync();
            }

            @Override
            public void onCancel() {
                onLoginFailed();
            }

            @Override
            public void onError(FacebookException exception) {
                onLoginFailed();
            }
        });

        Button notNowButton = (Button) view.findViewById(R.id.not_now);
        notNowButton.setVisibility(View.GONE);
        if (canBeSkipped) {
            notNowButton.setVisibility(View.VISIBLE);
            notNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BusProvider.getInstance().post(new LoginSkippedEvent());
                    BusProvider.getInstance().post(new HideFragmentEvent(Constants.TAG_LOGIN_FRAGMENT));
                }
            });
        }

        twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
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
                        user.setLocale(String.format("%s-%s", locale.getCountry().toLowerCase(), locale.getLanguage()).toLowerCase());
                        user.setCoverPicture(twitterUser.profileBannerUrl != null ? twitterUser.profileBannerUrl : twitterUser.profileBackgroundImageUrl);

                        TwitterAuthClient authClient = new TwitterAuthClient();
                        authClient.requestEmail(Twitter.getSessionManager().getActiveSession(), new Callback<String>() {
                            @Override
                            public void success(Result<String> result) {
                                user.setEmail(result.data);
                                LoginProviderFactory.setLoginProvider(TwitterLoginProvider.class.getCanonicalName());
                                ApiClient.getClient(getActivity()).createUser(user);

                                FlurryWrapper.logEvent(TrackingEvents.UserTwitterLogin);
                            }

                            @Override
                            public void failure(TwitterException e) {
                                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                                        false, null, null, null, null);
                                startActivityForResult(intent, Constants.REQUEST_ACCOUNT_EMAIL);
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

    private void setUserFbProfiledDataAndLogin(final User user, final String id) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("redirect", false);
        bundle.putString("type", "large");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+id+"/picture",
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        JSONObject json = response.getJSONObject();
                        String pictureUrl = null;
                        try {
                            pictureUrl = json.getJSONObject("data").getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(!TextUtils.isEmpty(pictureUrl)) {
                            user.setProfilePicture(pictureUrl);
                            setUserCover(id, user);
                        }
                    }
                }
        ).executeAsync();
    }

    private void setUserCover(String id, final User user) {
        Bundle params = new Bundle();
        params.putString("fields", "cover");
        params.putString("type", "large");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+id,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        JSONObject json = response.getJSONObject();
                        String coverUrl = null;
                        try {
                            coverUrl = json.getJSONObject("cover").getString("source");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(!TextUtils.isEmpty(coverUrl)) {
                            user.setCoverPicture(coverUrl);
                        }


                        user.setLoginType(FacebookLoginProvider.PROVIDER_NAME);
                        user.setLocale(String.format("%s-%s", locale.getCountry().toLowerCase(), locale.getLanguage()).toLowerCase());
                        LoginProviderFactory.setLoginProvider(FacebookLoginProvider.class.getCanonicalName());

                        ApiClient.getClient(getActivity()).createUser(user);
                        FlurryWrapper.logEvent(TrackingEvents.UserFacebookLogin);
                    }
                }
        ).executeAsync();
    }

    @OnClick(R.id.login_button)
    public void onTwitterLoginBtnClick() {
        if (!LoginProviderFactory.get(activity).isUserLoggedIn()) {
            isTwitterLogin = true;
            NavUtils.getInstance(activity).setOnBackBlocked(true);
        }
    }

    @OnClick(R.id.gplus_login_button)
    public void onGooglePlusLoginBtnClick() {
        googleApiClient.connect();
    }

    @OnClick(R.id.fb_login_button)
    public void onFacebookLoginBtnClick() {
        NavUtils.getInstance(activity).setOnBackBlocked(true);
        isFacebookLogin = true;
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
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onStart() {
        super.onStart();
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
                LoginProviderFactory.setLoginProvider(TwitterLoginProvider.class.getCanonicalName());
                ApiClient.getClient(getActivity()).createUser(user);

                FlurryWrapper.logEvent(TrackingEvents.UserTwitterLogin);
            } else {
                onLoginFailed();
            }
        } else if (requestCode == Constants.GPLUS_SIGN_IN && resultCode == -1) {
            googleApiClient.connect();
        } else if (requestCode == Constants.FACEBOOK_SIGN_IN) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            isFacebookLogin = false;
        }

        isTwitterLogin = false;
    }

    @Subscribe
    public void onUserCreated(UserCreatedApiEvent event) {
        Log.e(TAG, LoginProviderFactory.get(activity).getName());
        LoginProviderFactory.get(activity).login(event.getUser());
    }

    private void onLoginFailed() {
        if (!isAdded()) {
            return;
        }
        Toast.makeText(activity, R.string.login_canceled_text, Toast.LENGTH_LONG).show();
        NavUtils.getInstance(activity).setOnBackBlocked(false);
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

            LoginProviderFactory.setLoginProvider(GooglePlusLoginProvider.class.getCanonicalName());
            ApiClient.getClient(getActivity()).createUser(user);

            FlurryWrapper.logEvent(TrackingEvents.UserGooglePlusLogin);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(activity, "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(activity, Constants.GPLUS_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}
