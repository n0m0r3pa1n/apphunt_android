package com.apphunt.app.ui.fragments.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.users.FollowingsIdsList;
import com.apphunt.app.api.apphunt.models.users.NamesList;
import com.apphunt.app.auth.FacebookLoginProvider;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants.LoginProviders;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetFilterUsersApiEvent;
import com.apphunt.app.ui.adapters.friends.FriendsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.views.widgets.CustomFacebookButton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

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
public class FacebookFriends extends BaseFragment {

    private static final String TAG = FacebookFriends.class.getSimpleName();
    private static FacebookFriends instance;

    private AppCompatActivity activity;
    private CallbackManager callbackManager;
    private FriendsAdapter adapter;
    private boolean isLoginProvider = false;

    @InjectView(R.id.layout_login)
    RelativeLayout layoutLogin;

    @InjectView(R.id.layout_list)
    RelativeLayout layoutList;

    @InjectView(R.id.no_results)
    RelativeLayout noResults;

    @InjectView(R.id.fb_login_button)
    CustomFacebookButton fbLoginBtn;

    @InjectView(R.id.loader_friends)
    CircularProgressBar loader;

    @InjectView(R.id.fb_friends_list)
    RecyclerView friendsList;

    @InjectView(R.id.select_all)
    Button selectAll;

    @InjectView(R.id.follow)
    Button follow;

    public static FacebookFriends getInstance() {
        if (instance == null) {
            instance = new FacebookFriends();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LoginProviderFactory.get(activity) != null) {
            this.isLoginProvider = LoginProviderFactory.get(activity).getName().equals(FacebookLoginProvider.PROVIDER_NAME);
        }

        if (!isLoginProvider) {
            AccessToken.setCurrentAccessToken(null);
            Profile.setCurrentProfile(null);
            LoginManager.getInstance().logOut();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_facebook, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        if (isLoginProvider) {
            layoutLogin.setVisibility(View.GONE);
            layoutList.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);

            obtainAndFilterFriends();
        } else {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutList.setVisibility(View.GONE);
            loader.setVisibility(View.GONE);

            fbLoginBtn.setReadPermissions(Arrays.asList("user_friends", "email"));
            fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    layoutLogin.setVisibility(View.GONE);
                    obtainAndFilterFriends();
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e(TAG, error.getMessage());
                }
            });
        }

        friendsList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        friendsList.setLayoutManager(layoutManager);
        friendsList.setHasFixedSize(true);
    }

    private void obtainAndFilterFriends() {
        loader.setVisibility(View.VISIBLE);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        AccessToken.setCurrentAccessToken(null);
                        Profile.setCurrentProfile(null);
                        LoginManager.getInstance().logOut();

                        NamesList names = new NamesList();
                        try {
                            JSONArray data = response.getJSONObject().getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject user = data.getJSONObject(i);
                                names.addName(user.getString("name"));
                                ApiClient.getClient(activity).filterFriends(LoginProviderFactory.get(activity).getUser().getId(),
                                        names, LoginProviders.FACEBOOK);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
        ).executeAsync();
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
            followingsIdsList.addId(adapter.getSelectedFriends().get(i).getId());
        }

        followUsers(followingsIdsList);
    }

    private void followUsers(FollowingsIdsList ids) {
        ApiClient.getClient(activity).followUsers(LoginProviderFactory.get(activity).getUser().getId(), ids);
    }

    @Subscribe
    public void onObtainFilteredUsers(GetFilterUsersApiEvent event) {
        if (friendsList != null && event.getProvider().equals(LoginProviders.FACEBOOK)) {
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
        this.callbackManager = CallbackManager.Factory.create();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }
}
