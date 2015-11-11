package com.apphunt.app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.users.UserProfile;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.users.GetUserProfileApiEvent;
import com.apphunt.app.event_bus.events.api.users.UserFollowApiEvent;
import com.apphunt.app.event_bus.events.api.users.UserUnfollowApiEvent;
import com.apphunt.app.ui.adapters.profile.ProfileTabsPagerAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.fragments.profile.FollowersFragment;
import com.apphunt.app.ui.fragments.profile.FollowingsFragment;
import com.apphunt.app.ui.views.widgets.AHTextView;
import com.apphunt.app.ui.views.widgets.FollowButton;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends BackStackFragment {
    public static final String TAG = UserProfileFragment.class.getSimpleName();
    public static final String USER_ID = "PROFILE_ID";
    public static final String NAME = "NAME";

    private AppCompatActivity activity;
    private ProfileTabsPagerAdapter pagerAdapter;
    private MenuItem findFriendsAction;

    private String title;

    @InjectView(R.id.score)
    AHTextView score;

    @InjectView(R.id.name)
    AHTextView name;

    @InjectView(R.id.user_picture)
    CircleImageView userPicture;

    @InjectView(R.id.banner)
    ImageView banner;

    @InjectView(R.id.score_text)
    TextView scoreText;

    @InjectView(R.id.follow)
    FollowButton follow;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;

    @InjectView(R.id.profile_tabs)
    ViewPager profileTabsPager;

    @InjectView(R.id.followers_count)
    TextView followersCount;

    @InjectView(R.id.followings_count)
    TextView followingCount;

    @InjectView(R.id.score_container)
    LinearLayout scoreContainer;

    @InjectView(R.id.followings_container)
    LinearLayout followingsContainer;

    @InjectView(R.id.followers_container)
    LinearLayout followersContainer;

    @InjectView(R.id.follow_button_container)
    LinearLayout followBtnContainer;

    @InjectView(R.id.info_container)
    LinearLayout infoContainer;

    private int appsCount;
    private int collectionsCount;
    private int commentsCount;
    private int favouriteAppsCount;

    private int favouriteCollectionsCount;
    private int selectedTabPosition = 0;
    private String userId;
    private Calendar calendar = Calendar.getInstance();

    public static UserProfileFragment newInstance(final String userId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        bundle.putString(NAME, name);
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        FlurryWrapper.logEvent(TrackingEvents.UserViewedUserProfile, new HashMap<String, String>(){{
            put("profileId", userId);
        }});
        return userProfileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.inject(this, view);

        title = getArguments().getString(NAME);
        userId = getArguments().getString(USER_ID);

        pagerAdapter = new ProfileTabsPagerAdapter(getChildFragmentManager(), userId);
        updateAbSubtitle(selectedTabPosition);
        profileTabsPager.setOffscreenPageLimit(1);
        profileTabsPager.setAdapter(pagerAdapter);
        profileTabsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                selectedTabPosition = position;
                updateAbSubtitle(selectedTabPosition);
                FlurryWrapper.logEvent(TrackingEvents.UserViewedProfileSection, new HashMap<String, String>() {{
                    put("screen", pagerAdapter.getPageTitle(position).toString());
                }});
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        calendar.set(Calendar.DAY_OF_MONTH, 1);

//        ApiClient.getClient(activity).getUserProfile(userId, calendar.getTime(), new Date());
        return view;
    }

    @OnClick(R.id.followings_count)
    public void onFollowingsCountClick() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, FollowingsFragment.newInstance(userId), Constants.TAG_FOLLOWINGS_FRAGMENT)
                .addToBackStack(Constants.TAG_FOLLOWINGS_FRAGMENT)
                .commit();
    }

    @OnClick(R.id.label_followings)
    public void onFollowingsLabelClick() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, FollowingsFragment.newInstance(userId), Constants.TAG_FOLLOWINGS_FRAGMENT)
                .addToBackStack(Constants.TAG_FOLLOWINGS_FRAGMENT)
                .commit();
    }

    @OnClick(R.id.followers_count)
    public void onFollowersCountClick() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, FollowersFragment.newInstance(userId), Constants.TAG_FOLLOWERS_FRAGMENT)
                .addToBackStack(Constants.TAG_FOLLOWERS_FRAGMENT)
                .commit();
    }

    @OnClick(R.id.label_followers)
    public void onFollowersLabelClick() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, FollowersFragment.newInstance(userId), Constants.TAG_FOLLOWERS_FRAGMENT)
                .addToBackStack(Constants.TAG_FOLLOWERS_FRAGMENT)
                .commit();
    }

    private void updateAbSubtitle(int position) {
        if (position == -1) {
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
                return getResources().getQuantityString(R.plurals.apps, favouriteAppsCount, favouriteAppsCount);
            case 2:
                return getResources().getQuantityString(R.plurals.collections, collectionsCount, collectionsCount);
            case 3:
                return getResources().getQuantityString(R.plurals.collections, favouriteCollectionsCount, favouriteCollectionsCount);
            case 4:
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
                return favouriteAppsCount;
            case 2:
                return collectionsCount;
            case 3:
                return favouriteCollectionsCount;
            case 4:
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
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (findFriendsAction != null)
            findFriendsAction.setVisible(false);
    }

    @Override
    public void unregisterForEvents() {
        super.unregisterForEvents();
        ActionBarUtils.getInstance().setSubtitle("");
    }

    @Override
    public void registerForEvents() {
        super.registerForEvents();
        updateAbSubtitle(selectedTabPosition);

        ApiClient.getClient(activity).getUserProfile(userId, LoginProviderFactory.get(activity).getUser().getId(), calendar.getTime(), new Date());
    }

    public String getUserId() {
        return userId;
    }

    @Subscribe
    public void onUserProfileReceived(GetUserProfileApiEvent event) {
        UserProfile userProfile = event.getUserProfile();
        if (TextUtils.isEmpty(userProfile.getCoverPicture())) {
            banner.setImageResource(R.drawable.header_bg);
        } else {
            Picasso.with(activity).load(userProfile.getCoverPicture()).into(banner);
        }

        if (TextUtils.isEmpty(userProfile.getProfilePicture())) {
            userPicture.setImageResource(R.drawable.ic_contact_picture);
        } else {
            Picasso.with(activity).load(userProfile.getProfilePicture()).placeholder(R.drawable.placeholder_avatar)
                    .into(userPicture);
        }

        score.setText("" + userProfile.getScore());
        name.setText(userProfile.getName());

        appsCount = userProfile.getApps();
        collectionsCount = userProfile.getCollections();
        commentsCount = userProfile.getComments();
        favouriteAppsCount = userProfile.getFavouriteApps();
        favouriteCollectionsCount = userProfile.getFavouriteCollections();
        followingCount.setText(String.valueOf(userProfile.getFollowingCount()));
        followersCount.setText(String.valueOf(userProfile.getFollowersCount()));

        follow.init(activity, event.getUserProfile());

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        scoreText.setText(String.format(getString(R.string.points_month), StringUtils.getMonthStringFromCalendar(currentMonth, true)));

        if (LoginProviderFactory.get(activity).getUser().getId().equals(getUserId())) {
            followBtnContainer.setVisibility(View.GONE);
            infoContainer.setWeightSum(3.0f);

            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) followingsContainer.getLayoutParams();
            param.weight = 1.0f;

            scoreContainer.setLayoutParams(param);
            followersContainer.setLayoutParams(param);
            followingsContainer.setLayoutParams(param);
        }

        updateAbSubtitle(selectedTabPosition);
    }

    @Subscribe
    public void onFollowSuccess(UserFollowApiEvent event) {
        if (event.isSuccess() && event.getUserId().equals(LoginProviderFactory.get(activity).getUser().getId())) {
            followersCount.setText(String.valueOf(Integer.valueOf(followersCount.getText().toString()) + 1));
        }
    }

    @Subscribe
    public void onUnfollowSuccess(UserUnfollowApiEvent event) {
        if (event.isSuccess() && event.getUserId().equals(LoginProviderFactory.get(activity).getUser().getId())) {
            followingCount.setText(String.valueOf(Integer.valueOf(followingCount.getText().toString()) - 1));
        } else {
            followersCount.setText(String.valueOf(Integer.valueOf(followersCount.getText().toString()) - 1));
        }
    }

    private void initTabs() {
        tabLayout.setupWithViewPager(profileTabsPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        findFriendsAction = menu.findItem(R.id.action_find_friends);

        if (activity.getSupportFragmentManager().findFragmentById(R.id.container) instanceof UserProfileFragment
                && LoginProviderFactory.get(activity).isUserLoggedIn()
                && LoginProviderFactory.get(activity).getUser().getId().equals(userId)) {
            findFriendsAction.setVisible(true);
        } else {
            findFriendsAction.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_find_friends) {
            NavUtils.getInstance(activity).presentFindFriendsFragment();
        }
        return super.onOptionsItemSelected(item);
    }
}
