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
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.users.GetFollowersApiEvent;
import com.apphunt.app.ui.adapters.FollowersAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 10/2/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowersFragment extends BackStackFragment {

    private String profileId, userId;
    private int currentPage = 0;

    private AppCompatActivity activity;
    private FollowersAdapter adapter;

    @InjectView(R.id.followers)
    ScrollRecyclerView scrollRecyclerView;

    @InjectView(R.id.vs_no_followers)
    ViewStub vsNoFollowers;

    public static FollowersFragment newInstance(String profileId) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_USER_PROFILE, profileId);
        FollowersFragment fragment = new FollowersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent(TrackingEvents.UserViewedFollowers);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_followers, container, false);
        ButterKnife.inject(this, view);
        profileId = getArguments().getString(Constants.KEY_USER_PROFILE);
        if(LoginProviderFactory.get(activity).isUserLoggedIn()) {
            userId = LoginProviderFactory.get(activity).getUser().getId();
        }
        getFollowers();

        initUI();

        return view;
    }

    private void initUI() {
        scrollRecyclerView.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getFollowers();
            }
        });
    }

    @Override
    public int getTitle() {
        return R.string.title_user_followers;
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
    public void onFollowersReceived(GetFollowersApiEvent event) {
        if(event == null || event.getFollowers() == null || event.getFollowers().getFollowers() == null || event.getFollowers().getFollowers().size() == 0) {
            vsNoFollowers.setVisibility(View.VISIBLE);
            return;
        }

        if(adapter == null) {
            adapter = new FollowersAdapter(activity, event.getFollowers().getFollowers());
            scrollRecyclerView.setAdapter(adapter,  event.getFollowers().getTotalCount());
        } else {
            adapter.addItems(event.getFollowers().getFollowers());
        }
    }

    private void getFollowers() {
        currentPage++;
        ApiClient.getClient(activity).getFollowers(profileId, userId, currentPage, Constants.PAGE_SIZE);
    }
}
