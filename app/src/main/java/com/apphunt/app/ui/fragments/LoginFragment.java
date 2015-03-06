package com.apphunt.app.ui.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.models.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoadersUtils;
import com.google.android.gms.common.AccountPicker;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.List;
import java.util.Locale;

import retrofit.http.GET;
import retrofit.http.Query;

public class LoginFragment extends BaseFragment {

    private static final String TAG = LoginFragment.class.getName();

    private ActionBarActivity activity;

    private User user;
    private TwitterLoginButton loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_login);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (TwitterLoginButton) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadersUtils.showBottomLoader(activity, R.drawable.loader_white, false);
                ((MainActivity) activity).setOnBackBlocked(true);
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
                        user.setName(twitterUser.name);
                        user.setProfilePicture(twitterUser.profileImageUrl);
                        user.setLoginType(TwitterLoginProvider.PROVIDER_NAME);
                        Locale locale = getResources().getConfiguration().locale;
                        user.setLocale(String.format("%s-%s", locale.getCountry().toLowerCase(), locale.getLanguage()).toLowerCase());

                        appHuntTwitterApiClient.getFriendsService().getFriends(userResult.data.screenName, new Callback<Friends>() {
                            @Override
                            public void success(Result<Friends> objectResult) {
                                Log.d("FriendsResponse", objectResult.data.users.get(0).screenName);
                                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                                        false, null, null, null, null);
                                startActivityForResult(intent, Constants.REQUEST_ACCOUNT_EMAIL);
                            }

                            @Override
                            public void failure(TwitterException e) {
                                Log.d("GetFriendsError", e.getMessage(), e);
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

//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//        if (state.isOpened()) {
//            Bundle params = new Bundle();
//            params.putString("fields", "id,name,email,picture");
//            new Request(session, "/me", params, HttpMethod.GET, new Request.Callback() {
//                @Override
//                public void onCompleted(Response response) {
//                    if (response != null) {
//                        try {
//                            Locale locale = getResources().getConfiguration().locale;
//
//                            JSONObject jsonObject = response.getGraphObject().getInnerJSONObject();
//
//                            user.setName(jsonObject.getString("name"));
//                            user.setProfilePicture(jsonObject.getJSONObject("picture").getJSONObject("data").getString("url"));
//                            user.setLoginType(FacebookLoginProvider.PROVIDER_NAME);
//                            user.setLocale(String.format("%s-%s", locale.getCountry().toLowerCase(), locale.getLanguage()).toLowerCase());
//                            user.setEmail(jsonObject.getString("email"));
//
//                            if (TextUtils.isEmpty(user.getEmail())) {
//                                throw new NoUserEmailAddressException();
//                            }
//                            LoginProviderFactory.setLoginProvider(activity, new FacebookLoginProvider(activity));
//                            LoginProviderFactory.get(activity).login(user);
//                        } catch (Exception e) {
//                            Crashlytics.logException(e);
//
//                        } finally {
//                            activity.supportInvalidateOptionsMenu();
//                        }
//                    } else {
//                        NotificationsUtils.showNotificationFragment(activity, getString(R.string.notification_cannot_login), false, false);
//                    }
//                }
//            }).executeAsync();
//        } else if (state.isClosed()) {
//            AppSpice.createEvent(TrackingEvents.UserLoggedOut).track();
//            LoginProviderFactory.get(activity).logout();
//            activity.supportInvalidateOptionsMenu();
//        }
//
//    }
//
//    private Session.StatusCallback callback = new Session.StatusCallback() {
//        @Override
//        public void call(Session session, SessionState state, Exception exception) {
//            onSessionStateChange(session, state, exception);
//        }
//    };

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
            } else {
                onLoginFailed();
            }
        }
    }

    private void onLoginFailed() {
        Toast.makeText(getActivity(), R.string.login_canceled_text, Toast.LENGTH_LONG).show();
    }

    class AppHuntTwitterApiClient extends TwitterApiClient {
        public AppHuntTwitterApiClient(TwitterSession session) {
            super(session);
        }

        public FriendsService getFriendsService() {
            return getService(FriendsService.class);
        }
    }

    interface FriendsService {
        @GET("/1.1/friends/list.json")
        void getFriends(@Query("screen_name") String screenName, Callback<Friends> cb);
    }

    class Friends {
        List<com.twitter.sdk.android.core.models.User> users;
    }
}
