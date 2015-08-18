package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.users.UserProfile;
import com.apphunt.app.event_bus.events.api.users.GetUserProfileApiEvent;
import com.apphunt.app.ui.adapters.profile.ProfileTabsPagerAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.views.widgets.AHTextView;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends BackStackFragment {
    public static final String TAG = UserProfileFragment.class.getSimpleName();
    public static final String USER_ID = "CREATOR_ID";
    public static final String NAME = "NAME";

    private Activity activity;
    private ProfileTabsPagerAdapter pagerAdapter;
    private String title;

    @InjectView(R.id.apps_count)
    AHTextView appsCountView;

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

    private int appsCount;
    private int collectionsCount;
    private int commentsCount;

    private int selectedTabPosition = 0;

    public static UserProfileFragment newInstance(String userId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        bundle.putString(NAME, name);
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        return userProfileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.inject(this, view);
        title = getArguments().getString(NAME);
        String userId = getArguments().getString(USER_ID);

        pagerAdapter = new ProfileTabsPagerAdapter(getChildFragmentManager(), userId);
        updateAbSubtitle(selectedTabPosition);
        profileTabsPager.setOffscreenPageLimit(1);
        profileTabsPager.setAdapter(pagerAdapter);
        profileTabsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedTabPosition = position;
                updateAbSubtitle(selectedTabPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ApiClient.getClient(activity).getUserProfile(userId);

        return view;
    }

    private void updateAbSubtitle(int position) {
        if(position == -1) {
            ActionBarUtils.getInstance().setSubtitle("");
            return;
        }

        String title = getTitleForTab(position, getNumberForTabPosition(position));
        ActionBarUtils.getInstance().setSubtitle(title);

    }

    private String getTitleForTab(int position, int numberForTabPosition) {
        switch (position) {
            case 0:
                return getResources().getQuantityString(R.plurals.apps, appsCount, appsCount);
            case 1:
                return getResources().getQuantityString(R.plurals.collections, collectionsCount, collectionsCount);
            case 2:
                return getResources().getQuantityString(R.plurals.comments, commentsCount, commentsCount);
            default:
                return "";
        }
    }

    private int getNumberForTabPosition(int position) {
        switch (position) {
            case 0:
                return appsCount;
            case 1:
                return collectionsCount;
            case 2:
                return commentsCount;
            default:
                return 0;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateAbSubtitle(-1);
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
        appsCountView.setText(event.getUserProfile().getApps() + "");

        appsCount = userProfile.getApps();
        collectionsCount = userProfile.getCollections();
        commentsCount = userProfile.getComments();

        updateAbSubtitle(selectedTabPosition);
    }

    private void initTabs() {
        tabLayout.setupWithViewPager(profileTabsPager);
    }
}
