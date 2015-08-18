package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.apphunt.models.users.UserProfile;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.events.api.collections.GetMyCollectionsApiEvent;
import com.apphunt.app.event_bus.events.api.users.GetUserAppsApiEvent;
import com.apphunt.app.event_bus.events.api.users.GetUserCommentsApiEvent;
import com.apphunt.app.event_bus.events.api.users.GetUserProfileApiEvent;
import com.apphunt.app.ui.adapters.profile.ProfileTabsPagerAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.views.widgets.AHTextView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends BackStackFragment {
    public static final String TAG = UserProfileFragment.class.getSimpleName();

    private Activity activity;
    private ProfileTabsPagerAdapter pagerAdapter;
    private String title;

    @InjectView(R.id.apps_count)
    AHTextView appsCount;

    @InjectView(R.id.points)
    AHTextView points;

    @InjectView(R.id.username)
    AHTextView username;

    @InjectView(R.id.name)
    AHTextView name;

    @InjectView(R.id.user_picture)
    CircleImageView userPicture;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;

    @InjectView(R.id.profile_tabs)
    ViewPager profileTabsPager;

    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.inject(this, view);
        pagerAdapter = new ProfileTabsPagerAdapter(getChildFragmentManager());
        profileTabsPager.setOffscreenPageLimit(1);
        profileTabsPager.setAdapter(pagerAdapter);

        User user = LoginProviderFactory.get(activity).getUser();
        title = user.getName();
        String userId = user.getId();

        ApiClient.getClient(activity).getUserProfile(userId);
        ApiClient.getClient(activity).getUserComments(userId , new Pagination(1, 5));
        ApiClient.getClient(activity).getUserApps(userId , new Pagination(1, 5));
        ApiClient.getClient(activity).getMyCollections(userId, 1, 5);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabs();
    }

    @Override
    public String getStringTitle() {
        return title;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Subscribe
    public void onUserProfileReceived(GetUserProfileApiEvent event) {
        UserProfile userProfile = event.getUserProfile();
        Picasso.with(activity).load(userProfile.getProfilePicture()).into(userPicture);
        points.setText(userProfile.getScore() + " points");
        username.setText(userProfile.getUsername());
        name.setText(userProfile.getName());
        appsCount.setText(event.getUserProfile().getApps() + "");
    }

    @Subscribe
    public void onUserComments(GetUserCommentsApiEvent event) {
        Log.d(TAG, "onUserComments " + event.getComments().getComments().size());
        //ActionBarUtils.getInstance().setSubtitle("AAAAA");
    }

    @Subscribe
    public void onUserApps(GetUserAppsApiEvent event) {
        Log.d(TAG, "onUserApps " + event.getApps().getApps().size());
    }

    @Subscribe
    public void onMyCollections(GetMyCollectionsApiEvent event) {
        Log.d(TAG, "onUserCollections " + event.getAppsCollection().getCollections().size());
    }

    private void initTabs() {
        tabLayout.setupWithViewPager(profileTabsPager);
    }
}
