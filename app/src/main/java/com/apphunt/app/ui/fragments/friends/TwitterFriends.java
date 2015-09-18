package com.apphunt.app.ui.fragments.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.api.twitter.models.Friends;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.auth.models.Friend;
import com.apphunt.app.ui.adapters.friends.TwitterFriendsAdapter;
import com.apphunt.app.ui.adapters.invite.FriendsInviteAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.views.widgets.CustomTwitterLoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/17/15.
 * *
 * * NaughtySpirit 2015
 */
public class TwitterFriends extends BaseFragment {

    private static final String TAG = TwitterFriends.class.getSimpleName();

    private static TwitterFriends instance;

    private AppCompatActivity activity;
    private AppHuntTwitterApiClient twitterApiClient;
    private boolean isLoginProvider = false;

    @InjectView(R.id.layout_login)
    RelativeLayout layoutLogin;

    @InjectView(R.id.login_button)
    CustomTwitterLoginButton twitterLoginBtn;

    @InjectView(R.id.loader_friends)
    CircularProgressBar loader;

    @InjectView(R.id.friends_list)
    RecyclerView friendsList;

    public static TwitterFriends getInstance() {
        if (instance == null) {
            instance = new TwitterFriends();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LoginProviderFactory.get(activity) != null) {
            this.isLoginProvider = LoginProviderFactory.get(activity).getName().equals(TwitterLoginProvider.PROVIDER_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_twitter, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        Log.e(TAG, isLoginProvider + " " + LoginProviderFactory.get(activity).getName());
        if (isLoginProvider) {
            layoutLogin.setVisibility(View.GONE);
            friendsList.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);

            twitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
            obtainAndFilterFriends(LoginProviderFactory.get(activity).getUser().getUsername());
        } else {
            layoutLogin.setVisibility(View.VISIBLE);
            friendsList.setVisibility(View.GONE);
            loader.setVisibility(View.GONE);

            twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    twitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
                    Log.e(TAG, result.data.getUserName());
                    obtainAndFilterFriends(result.data.getUserName());

                    layoutLogin.setVisibility(View.GONE);
                    friendsList.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.VISIBLE);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.e(TAG, "Bla1 " +  e.getMessage());
                }
            });
        }
    }

    private void obtainAndFilterFriends(String username) {
        twitterApiClient.getFriendsService().getFriends(username, new Callback<Friends>() {
            @Override
            public void success(Result<Friends> result) {
               // TODO: Create server filter request
                for (User f : result.data.getUsers()) {
                    Log.e(TAG, f.idStr + "\n");
                }
            }

            @Override
            public void failure(TwitterException e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (twitterLoginBtn != null)
            twitterLoginBtn.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }
}
