package com.apphunt.app.ui.fragments.profile;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.events.api.users.GetFollowingsApiEvent;
import com.apphunt.app.ui.adapters.FollowersAdapter;
import com.apphunt.app.ui.adapters.dividers.SimpleDividerItemDecoration;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowingsFragment extends BackStackFragment {

    private AppCompatActivity activity;
    private FollowersAdapter adapter;

    private String profileId, userId;
    private int currentPage = 0;

    @InjectView(R.id.following)
    ScrollRecyclerView scrollRecyclerView;

    @InjectView(R.id.vs_no_followers)
    ViewStub vsNoFollowers;

    public static FollowingsFragment newInstance(String creatorId) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_USER_PROFILE, creatorId);
        FollowingsFragment fragment = new FollowingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_followings, container, false);
        ButterKnife.inject(this, view);
        profileId = getArguments().getString(Constants.KEY_USER_PROFILE);
        if(LoginProviderFactory.get(activity).isUserLoggedIn()) {
            userId = LoginProviderFactory.get(activity).getUser().getId();
        }
        getFollowings();

        initUI();

        return view;
    }

    private void initUI() {
        scrollRecyclerView.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(activity));
        scrollRecyclerView.showBottomLoader();
        scrollRecyclerView.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getFollowings();
            }
        });
    }

    @Override
    public int getTitle() {
        return R.string.title_user_followings;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Subscribe
    public void onFollowingReceived(GetFollowingsApiEvent event) {
        scrollRecyclerView.hideBottomLoader();
        if(event == null || event.getFollowing() == null || event.getFollowing().getFollowing() == null || event.getFollowing().getFollowing().size() == 0) {
            vsNoFollowers.setVisibility(View.VISIBLE);
            return;
        }

        if(adapter == null) {
            adapter = new FollowersAdapter(activity, event.getFollowing().getFollowing());
            scrollRecyclerView.setAdapter(adapter,  event.getFollowing().getTotalCount());
        } else {
            adapter.addItems(event.getFollowing().getFollowing());
        }
    }

    private void getFollowings() {
        currentPage++;
        ApiClient.getClient(activity).getFollowings(profileId, userId, currentPage, Constants.PAGE_SIZE);
    }
}
