package com.apphunt.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.clients.rest.ApiService;
import com.apphunt.app.api.apphunt.models.notifications.NotificationType;
import com.apphunt.app.api.apphunt.models.users.LoginType;
import com.apphunt.app.auth.AnonymousLoginProvider;
import com.apphunt.app.auth.LoginProvider;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.GetRandomAppApiEvent;
import com.apphunt.app.event_bus.events.api.version.GetAppVersionApiEvent;
import com.apphunt.app.event_bus.events.ui.CallToActionEvent;
import com.apphunt.app.event_bus.events.ui.ClearSearchEvent;
import com.apphunt.app.event_bus.events.ui.DisplayLoginFragmentEvent;
import com.apphunt.app.event_bus.events.ui.DrawerStatusEvent;
import com.apphunt.app.event_bus.events.ui.HideFragmentEvent;
import com.apphunt.app.event_bus.events.ui.NetworkStatusChangeEvent;
import com.apphunt.app.event_bus.events.ui.SelectFragmentEvent;
import com.apphunt.app.event_bus.events.ui.ShowNotificationEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.event_bus.events.ui.history.UnseenHistoryEvent;
import com.apphunt.app.event_bus.events.ui.votes.AppVoteEvent;
import com.apphunt.app.services.CommentAppService;
import com.apphunt.app.services.InstallService;
import com.apphunt.app.ui.fragments.AboutFragment;
import com.apphunt.app.ui.fragments.AppsFragment;
import com.apphunt.app.ui.fragments.CollectionsFragment;
import com.apphunt.app.ui.fragments.TopAppsFragment;
import com.apphunt.app.ui.fragments.TopHuntersFragment;
import com.apphunt.app.ui.fragments.actions.CallToActionDialogFragment;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.friends.FindFriendsFragment;
import com.apphunt.app.ui.fragments.help.AddAppFragment;
import com.apphunt.app.ui.fragments.help.AppsRequirementsFragment;
import com.apphunt.app.ui.fragments.login.LoginFragment;
import com.apphunt.app.ui.fragments.navigation.NavigationDrawerCallbacks;
import com.apphunt.app.ui.fragments.navigation.NavigationDrawerFragment;
import com.apphunt.app.ui.fragments.navigation.RightDrawerFragment;
import com.apphunt.app.ui.fragments.notification.SettingsFragment;
import com.apphunt.app.ui.fragments.notification.SuggestFragment;
import com.apphunt.app.ui.fragments.notification.UpdateRequiredFragment;
import com.apphunt.app.ui.fragments.posts.NewsFragment;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.PackagesUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.apptentive.android.sdk.Apptentive;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks {

    public static final String TAG = MainActivity.class.getSimpleName();
    private UpdateRequiredFragment dialog;
    private NavigationDrawerFragment navigationDrawerFragment;
    private RightDrawerFragment rightDrawerFragment;
    private Toolbar toolbar;
    private boolean consumedBack;
    private Boolean hasInternet = null;
    boolean isNetworkChanged = false;
    private int previousPosition = 0;
    private int versionCode;
    private MenuItem menuItemHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlurryWrapper.logEvent(TrackingEvents.UserOpenedApp);

        initUI();
        initDeepLinking();
        initNotifications();
        InstallService.setupService(this);
        CommentAppService.setupService(this);
        sendBroadcast(new Intent(Constants.ACTION_ENABLE_NOTIFICATIONS));
        ApiClient.getClient(this).getLatestAppVersionCode();
    }

    private void checkForTheLatestAppVersion() {
        int latestAppVersion = SharedPreferencesHelper.getIntPreference(Constants.KEY_LATEST_APP_VERSION, 0);
        if(latestAppVersion == 0) {
            return;
        }

        try {
            versionCode = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        boolean isDialogDisplayed = dialog != null && dialog.isAdded();
        if(versionCode >= latestAppVersion) {
            if(isDialogDisplayed) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
            }
            return;
        }


        if(isDialogDisplayed) {
            return;
        }

        dialog = UpdateRequiredFragment.newInstance();
        FlurryWrapper.logEvent(TrackingEvents.UserViewedUpdateAppDialog);
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "UpdateRequired");
    }

    private void initUI() {
        initToolbarAndNavigationDrawer();
        ActionBarUtils.getInstance().init(this);
        addBackStackChangeListener();
        showStartFragments(getIntent());
    }

    private int backStackCount = 0;

    private boolean isFragmentAdded(int currentBackStackCount) {
        return backStackCount < currentBackStackCount && currentBackStackCount > 1;
    }

    private void addBackStackChangeListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fragmentManager = getSupportFragmentManager();

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                int currentBackStackCount = fragmentManager.getBackStackEntryCount();
                if (currentBackStackCount > 0) {
                    FragmentManager.BackStackEntry e = fragmentManager.getBackStackEntryAt(currentBackStackCount - 1);
                    Fragment topFragment = fragmentManager.findFragmentByTag(e.getName());
                    if (topFragment instanceof BackStackFragment) {
                        ((BackStackFragment) topFragment).registerForEvents();
                        NavigationDrawerFragment.setDrawerIndicatorEnabled(true);
                        getSupportActionBar().collapseActionView();
                    }
                    BusProvider.getInstance().post(new DrawerStatusEvent(true));
                } else if (currentBackStackCount == 0) {
                    NavigationDrawerFragment.setDrawerIndicatorEnabled(false);
                    BusProvider.getInstance().post(new DrawerStatusEvent(false));
                }

                if (isFragmentAdded(currentBackStackCount)) {
                    FragmentManager.BackStackEntry previousEntry = fragmentManager.getBackStackEntryAt(currentBackStackCount - 2);
                    Fragment previousFragment = fragmentManager.findFragmentByTag(previousEntry.getName());
                    if (previousFragment instanceof BackStackFragment) {
                        ((BackStackFragment) previousFragment).unregisterForEvents();
                    }
                }

                backStackCount = currentBackStackCount;

                setActionBarTitle();
                supportInvalidateOptionsMenu();
            }
        });
    }

    private void setActionBarTitle() {
        BaseFragment currentFragment = ((BaseFragment) getSupportFragmentManager().findFragmentById(R.id.container));
        if (currentFragment.getTitle() == 0) {
            getSupportActionBar().setTitle(currentFragment.getStringTitle());
        } else {
            getSupportActionBar().setTitle(currentFragment.getTitle());
        }
    }

    private void initToolbarAndNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        rightDrawerFragment = (RightDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_right_drawer);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                if (backStackEntryCount == 0) {
                    if (!navigationDrawerFragment.isDrawerOpen()) {
                        navigationDrawerFragment.openDrawer();
                    } else {
                        navigationDrawerFragment.closeDrawer();
                    }

                    return;
                }

                for (int i = 0; i < backStackEntryCount; i++) {
                    getSupportFragmentManager().popBackStack();
                }
            }
        });
        navigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer));
        rightDrawerFragment.setup((DrawerLayout) findViewById(R.id.drawer));
        onNavigationDrawerItemSelected(Constants.TRENDING_APPS);
    }

    private void initNotifications() {
        String notificationType = getIntent().getStringExtra(Constants.KEY_NOTIFICATION_TYPE);
        if (!TextUtils.isEmpty(notificationType)) {
            sendNotificationStatsToFlurry(notificationType);

            NotificationType type = NotificationType.getType(notificationType);

            switch (type) {
                case USER_COMMENT:
                case USER_MENTIONED:
                    String appId = getIntent().getStringExtra(Constants.KEY_NOTIFICATION_APP_ID);
                    NavUtils.getInstance(this).presentAppDetailsFragment(appId);
                    break;
                case TOP_APPS:
                    onNavigationDrawerItemSelected(Constants.TOP_APPS);
                    break;
                case TOP_HUNTER:
                    onNavigationDrawerItemSelected(Constants.TOP_HUNTERS);
                    break;
                case INSTALL:
                    displaySaveAppFragment();
                    break;
                case COMMENT_APP:
                    displayCommentAppScreen();
                    break;

            }

        }

        NotificationsUtils.updateNotificationIdIfNeeded(this);
    }

    private void sendNotificationStatsToFlurry(String notificationType) {
        Map<String, String> params = new HashMap<>();
        params.put("type", notificationType);
        FlurryWrapper.logEvent(TrackingEvents.UserStartedAppFromNotification, params);
    }

    private void initDeepLinking() {
        if (isStartedFromDeepLink()) {
            String action = getIntent().getAction();
            Uri data = getIntent().getData();

            Log.d("DeepLink Action", action);
            Log.d("DeepLink Data", data.toString());
        }
    }


    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constants.TAG_NOTIFICATION_FRAGMENT);
            if (ConnectivityUtils.isNetworkAvailable(context)) {
                setHasInternet(true);

                if (fragment != null) {
                    getSupportFragmentManager().popBackStack(Constants.TAG_NOTIFICATION_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                if (isNetworkChanged) {
                    BusProvider.getInstance().post(new NetworkStatusChangeEvent(true));
                }
            } else {
                setHasInternet(false);
                if (fragment == null) {
                    NotificationsUtils.showNotificationFragment(MainActivity.this, getString(R.string.notification_no_internet), true, false);
                }
            }
        }
    };

    private void setHasInternet(boolean hasInternet) {
        if (this.hasInternet == null || this.hasInternet == hasInternet) {
            isNetworkChanged = false;
        } else if (this.hasInternet != hasInternet) {
            isNetworkChanged = true;
        }
        this.hasInternet = hasInternet;
    }

    private boolean isStartedFromDeepLink() {
        Intent intent = getIntent();
        return intent != null && Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showStartFragments(intent);
        this.setIntent(intent);
        Log.e(TAG, intent.getType() + " " + intent.getAction() + " " + intent.getDataString());
    }

    private boolean isStartedFromShareIntent(Intent intent) {
        return Intent.ACTION_SEND.equals(intent.getAction());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int backstackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        boolean isTrendingAppsScreenVisible = getSupportFragmentManager().findFragmentByTag(Constants.TAG_APPS_LIST_FRAGMENT) != null;
        boolean isTopHuntersScreenVisible = getSupportFragmentManager().findFragmentByTag(Constants.TAG_TOP_HUNTERS_FRAGMENT) != null;
        boolean isTopAppsVisible = getSupportFragmentManager().findFragmentByTag(Constants.TAG_TOP_APPS_FRAGMENT) != null;
        if (backstackEntryCount == 0 &&
                isTrendingAppsScreenVisible) {
            menu.findItem(R.id.action_search).setVisible(true);
            menu.findItem(R.id.action_random).setVisible(true);
            menuItemHistory = menu.findItem(R.id.action_history);

            TextView eventsCountTextView = (TextView) MenuItemCompat.getActionView(menuItemHistory).findViewById(R.id.new_events_count);
            if(eventCount <= 0) {
                eventsCountTextView.setVisibility(View.INVISIBLE);
            } else {
                eventsCountTextView.setVisibility(View.VISIBLE);
                eventsCountTextView.setText(eventCount + "");
            }

            MenuItemCompat.getActionView(menuItemHistory).findViewById(R.id.action_history_container).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (rightDrawerFragment.isDrawerOpen()) {
                        rightDrawerFragment.closeDrawer();
                    } else {
                        FlurryWrapper.logEvent(TrackingEvents.UserOpenedHistory);
                        rightDrawerFragment.openDrawer();
                    }
                }
            });
            menuItemHistory.setVisible(true);


            menu.findItem(R.id.action_random).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    LoginProvider loginProvider = LoginProviderFactory.get(MainActivity.this);
                    String userId = loginProvider.isUserLoggedIn() ? loginProvider.getUser().getId() : "";
                    FlurryWrapper.logEvent(TrackingEvents.UserOpenedRandomApp, new HashMap<String, String>(){{
                        put("screen", TAG);
                    }});
                    ApiClient.getClient(MainActivity.this).getRandomApp(userId);
                    return true;
                }
            });
        } else {
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_random).setVisible(false);
            menu.findItem(R.id.action_history).setVisible(false);
        }

        if (backstackEntryCount > 0 &&
                getSupportFragmentManager().findFragmentByTag(CollectionsFragment.TAG) != null) {
            menu.findItem(R.id.action_sort).setVisible(false);
        }

        if(backStackCount == 0 && (isTopAppsVisible || isTopHuntersScreenVisible)) {
            menu.findItem(R.id.action_date_picker).setVisible(true);
        } else {
            menu.findItem(R.id.action_date_picker).setVisible(false);
        }

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!isFinishing()) {
                    NavUtils.getInstance(MainActivity.this).presentSearchResultsFragment(s);
                }

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    BusProvider.getInstance().post(new ClearSearchEvent());
                }
                return true;
            }
        });

        searchView.setIconifiedByDefault(true);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
        }
        return false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if(previousPosition == position) {
            return;
        }
        ApiClient.getClient(this).cancelAllRequests();

        BaseFragment fragment = null;
        boolean addToBackStack = false;
        switch (position) {
            case Constants.TRENDING_APPS:
                fragment = new AppsFragment();
                break;
            case Constants.TOP_APPS:
                fragment = new TopAppsFragment();
                break;
            case Constants.TOP_HUNTERS:
                fragment = new TopHuntersFragment();
                break;
            case Constants.COLLECTIONS:
                fragment = new CollectionsFragment();
                break;
            case Constants.NEWS:
                fragment = new NewsFragment();
                break;
            case Constants.ABOUT:
                fragment = new AboutFragment();
                break;
            case Constants.SUGGESTIONS:
                fragment = new SuggestFragment();
                fragment.setPreviousTitle(toolbar.getTitle() != null ? toolbar.getTitle().toString() : "");
                addToBackStack = true;
                break;
            case Constants.SETTINGS:
                fragment = new SettingsFragment();
                fragment.setPreviousTitle(toolbar.getTitle() != null ? toolbar.getTitle().toString() : "");
                addToBackStack = true;
                break;
            case Constants.HELP_ADD_APP:
                fragment = new AddAppFragment();
                break;
            case Constants.HELP_TOP_HUNTERS_POINTS:
                fragment = new com.apphunt.app.ui.fragments.help.TopHuntersFragment();
                break;
            case Constants.HELP_APPS_REQUIREMENTS:
                fragment = new AppsRequirementsFragment();
                break;
        }

        if(fragment == null) {
            return;
        }

        consumedBack = position == Constants.TRENDING_APPS;

        try {
            if (!addToBackStack) {
                getSupportActionBar().setTitle(fragment.getTitle());
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, fragment.getFragmentTag()).commit();
                navigationDrawerFragment.markSelectedPosition(position);
                previousPosition = position;
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0 ||
                        !getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                                .getName().equals(fragment.getFragmentTag())) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                            .add(R.id.container, fragment, fragment.getFragmentTag())
                            .addToBackStack(fragment.getFragmentTag())
                            .commit();
                }
                navigationDrawerFragment.markSelectedPosition(previousPosition);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.SHOW_INVITE && resultCode == Constants.SHOW_LOGIN) {
            LoginUtils.showLoginFragment(false);
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constants.TAG_LOGIN_FRAGMENT);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        fragment = getSupportFragmentManager().findFragmentByTag(Constants.TAG_FIND_FRIENDS_FRAGMENT);
        if(fragment != null){
            ((FindFriendsFragment) fragment).onReceivedActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        BusProvider.getInstance().register(this);
        checkForTheLatestAppVersion();
    }

    private void displayCommentAppScreen() {
        String appId = getIntent().getStringExtra(Constants.EXTRA_APP_ID);
        if(!TextUtils.isEmpty(appId)) {
            FlurryWrapper.logEvent(TrackingEvents.UserViewedCommentAppFromNotification);
            NavUtils.getInstance(this).presentAppDetailsFragment(appId);
        }
    }

    private void displaySaveAppFragment() {
        String appPackage = getIntent().getStringExtra(Constants.EXTRA_APP_PACKAGE);
        if(!TextUtils.isEmpty(appPackage)) {
            FlurryWrapper.logEvent(TrackingEvents.UserViewedSaveAppFragmentFromNotification);
            ApplicationInfo data = PackagesUtils.getApplicationInfo(getPackageManager(), appPackage);
            NavUtils.getInstance(this).presentSaveAppFragment(this, data);
        }
    }

    private void showStartFragments(Intent intent) {
        if (isStartedFromShareIntent(intent)) {
            NavUtils.getInstance(this).startSelectAppFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
            return;
        }

        if(rightDrawerFragment.isDrawerOpen()) {
            rightDrawerFragment.closeDrawer();
            return;
        }

        if (NavUtils.getInstance(this).isOnBackBlocked()) {
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0 && !consumedBack) {
            FlurryWrapper.logEvent(TrackingEvents.UserReturnedToTrendingApps);
            onNavigationDrawerItemSelected(Constants.TRENDING_APPS);
            consumedBack = true;
            return;
        }

        FlurryWrapper.logEvent(TrackingEvents.UserPressedBackButton);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Apptentive.onStart(this);

        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    try {
                        if (referringParams.has(Constants.KEY_DL_TYPE) && referringParams.getString(Constants.KEY_DL_TYPE).equals("welcome")
                                && !LoginProviderFactory.get(MainActivity.this).isUserLoggedIn()) {
                            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                            intent.putExtra(Constants.KEY_SENDER_ID, referringParams.getString(Constants.KEY_SENDER_ID));
                            intent.putExtra(Constants.KEY_SENDER_NAME, referringParams.getString(Constants.KEY_SENDER_NAME));
                            intent.putExtra(Constants.KEY_SENDER_PROFILE_IMAGE_URL, referringParams.getString(Constants.KEY_SENDER_PROFILE_IMAGE_URL));
                            String receiverName = referringParams.has(Constants.KEY_RECEIVER_NAME) ? referringParams.getString(Constants.KEY_RECEIVER_NAME) : null;
                            intent.putExtra(Constants.KEY_RECEIVER_NAME, receiverName);
                            startActivityForResult(intent, Constants.SHOW_INVITE);
                            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                        }

                        if (!TextUtils.isEmpty(referringParams.getString(Constants.KEY_BRANCH_APP_ID))) {
                            FlurryWrapper.logEvent(TrackingEvents.UserViewedAppDetailsFromSharedLink);
                            NavUtils.getInstance(MainActivity.this)
                                    .presentAppDetailsFragment(referringParams.getString(Constants.KEY_BRANCH_APP_ID));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (IllegalArgumentException e) {
            Crashlytics.logException(e);
        }
        Apptentive.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiService.resetCalendars();
    }

    @Subscribe
    public void onRandomAppReceived(GetRandomAppApiEvent event) {
        NavUtils.getInstance(this).presentAppDetailsFragment(event.getApp().getId());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void userVotedForAppEvent(AppVoteEvent event) {
        if (event.isVote()) {
            Apptentive.engage(this, "user.voted");
        }
    }

    private int eventCount = 0;
    @Subscribe
    public void onUnseenEvents(final UnseenHistoryEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (menuItemHistory == null) {
                    return;
                }

                TextView textView = (TextView) MenuItemCompat.getActionView(menuItemHistory).findViewById(R.id.new_events_count);
                if (event.getCount() == 0) {
                    eventCount = 0;
                    textView.setVisibility(View.INVISIBLE);
                } else {
                    eventCount += event.getCount();
                    textView.setVisibility(View.VISIBLE);
                }
                textView.setText(eventCount + "");
            }
        });

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onUserLogin(LoginEvent event) {
        supportInvalidateOptionsMenu();
        NotificationsUtils.updateNotificationIdIfNeeded(this);

        NavUtils.getInstance(this).setOnBackBlocked(false);
        if(event.getUser().getLoginType().equals(LoginType.Twitter.toString())) {
            NavUtils.getInstance(this).presentInvitesScreen();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }



    @Subscribe
    public void onUserLogout(LogoutEvent event) {
        LoginProviderFactory.setLoginProvider(new AnonymousLoginProvider(this));
        hideLoginFragment();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onHideFragmentEvent(HideFragmentEvent event) {
        getSupportFragmentManager().popBackStack(event.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void showNotificationFragment(ShowNotificationEvent event) {
        NotificationsUtils.showNotificationFragment(this, event.getMessage(), false, true, event.shouldPopBackStack());
    }

    @Subscribe
    public void compareAppVersionWithLatest(GetAppVersionApiEvent event) {
        if(event == null || event.getVersion() == null) {
            return;
        }

        SharedPreferencesHelper.setPreference(Constants.KEY_LATEST_APP_VERSION, event.getVersion().getVersionCode());
        checkForTheLatestAppVersion();
    }

    @Subscribe
    public void displayLoginFragment(DisplayLoginFragmentEvent event) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setCanBeSkipped(event.canBeSkipped());
        String message = event.getMessage();
        if(TextUtils.isEmpty(message) && event.getMessageResId() != 0) {
            message = getString(event.getMessageResId());
        }
        loginFragment.setMessage(TextUtils.isEmpty(message) ? getString(R.string.login_info_text) : message);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                .add(R.id.container, loginFragment, Constants.TAG_LOGIN_FRAGMENT)
                .addToBackStack(Constants.TAG_LOGIN_FRAGMENT)
                .commit();
    }

    @Subscribe
    public void onSelectFragment(SelectFragmentEvent event) {
        onNavigationDrawerItemSelected(event.getDrawerFragmentIndex());
    }

    @Subscribe
    public void onCallToAction(CallToActionEvent event) {
        CallToActionDialogFragment fragment = new CallToActionDialogFragment();
        fragment.show(getSupportFragmentManager(), "CallToAction");
    }

    private void hideLoginFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(Constants.TAG_LOGIN_FRAGMENT);

        if (loginFragment != null) {
            fragmentManager.popBackStack();
        }
    }
}