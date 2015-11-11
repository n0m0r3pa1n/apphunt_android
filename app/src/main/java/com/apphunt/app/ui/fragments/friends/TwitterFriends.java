package com.apphunt.app.ui.fragments.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.users.FollowingsIdsList;
import com.apphunt.app.api.apphunt.models.users.NamesList;
import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.api.twitter.models.Friends;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.Constants.LoginProviders;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetFilterUsersApiEvent;
import com.apphunt.app.ui.adapters.friends.FriendsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.views.widgets.CustomTwitterLoginButton;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
    private FriendsAdapter adapter;
    private boolean isLoginProvider = false;

    @InjectView(R.id.layout_login)
    RelativeLayout layoutLogin;

    @InjectView(R.id.layout_list)
    RelativeLayout layoutList;

    @InjectView(R.id.no_results)
    RelativeLayout noResults;

    @InjectView(R.id.login_button)
    CustomTwitterLoginButton twitterLoginBtn;

    @InjectView(R.id.loader_friends)
    CircularProgressBar loader;

    @InjectView(R.id.tw_friends_list)
    RecyclerView friendsList;

    @InjectView(R.id.select_all)
    Button selectAll;

    @InjectView(R.id.follow)
    Button follow;

    public static TwitterFriends getInstance() {
        if (instance == null) {
            instance = new TwitterFriends();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryWrapper.logEvent(TrackingEvents.UserViewedTwitterSuggestions);

        if (LoginProviderFactory.get(activity) != null) {
            this.isLoginProvider = LoginProviderFactory.get(activity).getName().equals(TwitterLoginProvider.PROVIDER_NAME);
        }

        setFragmentTag(Constants.TAG_FIND_TWITTER_FRIENDS);
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
        if (isLoginProvider) {
            layoutLogin.setVisibility(View.GONE);
            layoutList.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);

            twitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
            obtainAndFilterFriends(LoginProviderFactory.get(activity).getUser().getUsername());
        } else {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutList.setVisibility(View.GONE);
            loader.setVisibility(View.GONE);

            twitterLoginBtn.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    layoutLogin.setVisibility(View.GONE);

                    twitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
                    obtainAndFilterFriends(result.data.getUserName());
                }

                @Override
                public void failure(TwitterException e) {
                }
            });
        }

        friendsList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        friendsList.setLayoutManager(layoutManager);
        friendsList.setHasFixedSize(true);
    }

    private void obtainAndFilterFriends(String username) {
        loader.setVisibility(View.VISIBLE);
        twitterApiClient.getFriendsService().getFriends(username, new Callback<Friends>() {
            @Override
            public void success(Result<Friends> result) {
                NamesList names = new NamesList();
                for (User f : result.data.getUsers()) {
                    names.addName(f.name);
                }
                ApiClient.getClient(activity).filterFriends(LoginProviderFactory.get(activity).getUser().getId(),
                        names, LoginProviders.TWITTER);
            }

            @Override
            public void failure(TwitterException e) {
            }
        });
    }

    @OnClick(R.id.select_all)
    public void onSelectAllClick() {
        if (adapter == null) {
            return;
        }

        adapter.selectAllItems();
    }

    @OnClick(R.id.follow)
    public void onFollowClick() {
        if (adapter == null) {
            return;
        }

        FollowingsIdsList followingsIdsList = new FollowingsIdsList();

        for (int i = 0; i < adapter.getSelectedFriends().size(); i++) {
            final String followingId = adapter.getSelectedFriends().get(i).getId();
            followingsIdsList.addId(followingId);
            FlurryWrapper.logEvent(TrackingEvents.UserFollowedSomeone, new HashMap<String, String>() {{
                put("followingId", followingId);
            }});
        }

        followUsers(followingsIdsList);
    }

    private void followUsers(FollowingsIdsList ids) {
        ApiClient.getClient(activity).followUsers(LoginProviderFactory.get(activity).getUser().getId(), ids);
    }

    @Subscribe
    public void onObtainFilteredUsers(GetFilterUsersApiEvent event) {
        if (friendsList != null && event.getProvider().equals(Constants.LoginProviders.TWITTER)) {
            loader.setVisibility(View.GONE);


            adapter = new FriendsAdapter(activity, event.getUsers());
            if (adapter.getItemCount() == 0) {
                noResults.setVisibility(View.VISIBLE);
                return;
            }

            friendsList.setAdapter(adapter);
            layoutList.setVisibility(View.VISIBLE);
        }
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
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }
}
