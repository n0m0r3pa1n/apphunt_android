package com.apphunt.app.ui.fragments.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.auth.BaseLoginProvider;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.models.Friend;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.ui.adapters.login.FriendsInviteAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.utils.DeepLinkingUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.client.Response;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/24/15.
 * *
 * * NaughtySpirit 2015
 */
public class ProviderInviteFragment extends BaseFragment {

    private static final String TAG = ProviderInviteFragment.class.getSimpleName();

    private AppCompatActivity activity;
    private FriendsInviteAdapter adapter;
    private int successfulInvites = 0;

    @InjectView(R.id.friends_list)
    RecyclerView friendsList;
    @InjectView(R.id.loader_friends)
    CircularProgressBar loader;

    public static ProviderInviteFragment newInstance() {
        ProviderInviteFragment fragment = new ProviderInviteFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginProviderFactory.get(activity).loadFriends(new BaseLoginProvider.OnFriendsResultListener() {
            @Override
            public void onFriendsReceived(ArrayList<Friend> friends) {
                loader.progressiveStop();
                loader.setVisibility(View.INVISIBLE);
                adapter = new FriendsInviteAdapter(activity, friends);
                friendsList.setAdapter(adapter);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_provider, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        loader.setVisibility(View.VISIBLE);

        friendsList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        friendsList.setLayoutManager(layoutManager);
        friendsList.setHasFixedSize(true);
    }

    @OnClick(R.id.send)
    public void onSendClick() {
        if (adapter == null) {
            return;
        }

        for (int i = 0; i < adapter.getSelectedFriends().size(); i++) {
            if (i == adapter.getSelectedFriends().size() - 1) {
                sendInvite(adapter.getSelectedFriends().get(i), true);
            } else {
                sendInvite(adapter.getSelectedFriends().get(i), false);
            }
        }
    }

    @OnClick(R.id.select_all)
    public void onSelectAllClick() {
        if (adapter == null) {
            return;
        }

        adapter.selectAllItems();
    }

    private void sendInvite(Friend friend, final Boolean isLastInvite) {
        ArrayList<DeepLinkingUtils.DeepLinkingParam> params = new ArrayList<>();
        params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_DL_TYPE, "welcome"));
        params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_ID, LoginProviderFactory.get(activity).getUser().getId()));
        params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_NAME, LoginProviderFactory.get(activity).getUser().getName()));
        params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_PROFILE_IMAGE_URL, LoginProviderFactory.get(activity).getUser().getProfilePicture()));
        params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_RECEIVER_NAME, friend.getName()));

        AppHuntTwitterApiClient twitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
        twitterApiClient.getFriendsService().sendDirectMessage(friend.getId(),
                "Hey, " + friend.getName() + "! Check out AppHunt cool app: " + DeepLinkingUtils.getInstance(activity).generateShortUrl(params), new Callback<Response>() {
                    @Override
                    public void success(Result<Response> result) {
                        successfulInvites += 1;

                        if (isLastInvite) {
                            activity.getSupportFragmentManager().popBackStack();
                            NotificationsUtils.showNotificationFragment((ActionBarActivity) activity, String.format(getString(R.string.msg_successful_invites), successfulInvites), false, false);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        if (isLastInvite) {
                            activity.getSupportFragmentManager().popBackStack();
                            NotificationsUtils.showNotificationFragment((ActionBarActivity) activity, String.format(getString(R.string.msg_failure_invites), successfulInvites), false, false);
                        }
                    }
                });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }
}
