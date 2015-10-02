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
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.clients.rest.AppHuntApiClient;
import com.apphunt.app.api.apphunt.models.users.UserProfile;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.events.api.users.GetUserProfileApiEvent;
import com.apphunt.app.ui.adapters.profile.ProfileTabsPagerAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.views.widgets.AHTextView;
import com.apphunt.app.ui.views.widgets.FollowButton;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends BackStackFragment {
    public static final String TAG = UserProfileFragment.class.getSimpleName();
    public static final String USER_ID = "CREATOR_ID";
    public static final String NAME = "NAME";

    private AppCompatActivity activity;
    private ProfileTabsPagerAdapter pagerAdapter;
    private MenuItem findFriendsAction;

    private String title;

    @InjectView(R.id.score)
    AHTextView score;

    @InjectView(R.id.username)
    AHTextView username;

    @InjectView(R.id.name)
    AHTextView name;

    @InjectView(R.id.user_picture)
    CircleImageView userPicture;

    @InjectView(R.id.banner)
    ImageView banner;

    @InjectView(R.id.follow)
    FollowButton follow;

    @InjectView(R.id.score_month)
    TextView scoreMonth;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;

    @InjectView(R.id.profile_tabs)
    ViewPager profileTabsPager;

    private int appsCount;
    private int collectionsCount;
    private int commentsCount;
    private int favouriteAppsCount;

    private int favouriteCollectionsCount;
    private int selectedTabPosition = 0;
    private String userId;

    public static UserProfileFragment newInstance(String userId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        bundle.putString(NAME, name);
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
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
            public void onPageSelected(int position) {
                selectedTabPosition = position;
                updateAbSubtitle(selectedTabPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        ApiClient.getClient(activity).getUserProfile(userId, calendar.getTime(), new Date());
        return view;
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
        username.setText(userProfile.getUsername());
        name.setText(userProfile.getName());

        appsCount = userProfile.getApps();
        collectionsCount = userProfile.getCollections();
        commentsCount = userProfile.getComments();
        favouriteAppsCount = userProfile.getFavouriteApps();
        favouriteCollectionsCount = userProfile.getFavouriteCollections();

        follow.init(activity, event.getUserProfile());

        scoreMonth.setText("(" + StringUtils.getMonthStringFromCalendar(0) + ")");

        updateAbSubtitle(selectedTabPosition);

        ApiClient.getClient(activity).getFollowers(userProfile.getId(), 1, 10);
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
