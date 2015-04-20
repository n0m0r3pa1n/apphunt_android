package com.apphunt.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;

import com.apphunt.app.api.apphunt.AppHuntApiClient;
import com.apphunt.app.api.apphunt.Callback;
import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.smart_rate.SmartRate;
import com.apphunt.app.smart_rate.variables.RateDialogVariable;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.ui.fragments.AppDetailsFragment;
import com.apphunt.app.ui.fragments.SaveAppFragment;
import com.apphunt.app.ui.fragments.SelectAppFragment;
import com.apphunt.app.ui.fragments.SettingsFragment;
import com.apphunt.app.ui.fragments.SuggestFragment;
import com.apphunt.app.ui.interfaces.OnAppSelectedListener;
import com.apphunt.app.ui.interfaces.OnAppVoteListener;
import com.apphunt.app.ui.interfaces.OnNetworkStateChange;
import com.apphunt.app.ui.interfaces.OnUserAuthListener;
import com.apphunt.app.utils.ActionBarUtils;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.FacebookUtils;
import com.apphunt.app.utils.LoadersUtils;
import com.apphunt.app.utils.NotificationsUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.facebook.widget.FacebookDialog;
import com.flurry.android.FlurryAgent;
import com.quentindommerc.superlistview.SuperListview;
import com.shamanland.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import it.appspice.android.AppSpice;
import it.appspice.android.api.errors.AppSpiceError;
import kr.nectarine.android.fruitygcm.FruityGcmClient;
import kr.nectarine.android.fruitygcm.interfaces.FruityGcmListener;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener, OnClickListener,
        OnAppSelectedListener, OnUserAuthListener, OnNetworkStateChange, OnAppVoteListener {

    private SuperListview trendingAppsList;
    private FloatingActionButton addAppButton;
    private Button reloadButton;
    private TrendingAppsAdapter trendingAppsAdapter;
    private boolean endOfList = false;
    private boolean isBlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmartRate.init(this, Constants.APP_SPICE_APP_ID);

        boolean isStartedFromNotification = getIntent().getBooleanExtra(Constants.KEY_DAILY_REMINDER_NOTIFICATION, false);
        if (isStartedFromNotification) {
            FlurryAgent.logEvent(TrackingEvents.UserStartedAppFromDailyTrendingAppsNotification);
        }

        updateNotificationIdIfNeeded();

        initUI();

        sendBroadcast(new Intent(Constants.ACTION_ENABLE_NOTIFICATIONS));
        showStartFragments(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showStartFragments(intent);
    }

    private boolean isStartedFromShareIntent(Intent intent) {
        return Intent.ACTION_SEND.equals(intent.getAction());
    }

    private void initUI() {
        ActionBarUtils.getInstance().init(this);

        addAppButton = (FloatingActionButton) findViewById(R.id.add_app);
        addAppButton.setOnClickListener(this);

        trendingAppsList = (SuperListview) findViewById(R.id.trending_list);

        trendingAppsAdapter = new TrendingAppsAdapter(this, trendingAppsList);
        trendingAppsList.setAdapter(trendingAppsAdapter);
        trendingAppsList.setOnScrollListener(this);
        trendingAppsList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportActionBar().collapseActionView();
                trendingAppsAdapter.resetAdapter();
            }
        });

        reloadButton = (Button) findViewById(R.id.reload);
        reloadButton.setOnClickListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                ActionBarUtils.getInstance().configActionBar(MainActivity.this);
            }
        });
    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constants.TAG_NOTIFICATION_FRAGMENT);

            if (!ConnectivityUtils.isNetworkAvailable(context)) {
                if (fragment == null) {
                    NotificationsUtils.showNotificationFragment(((ActionBarActivity) context), getString(R.string.notification_no_internet), true, false);
                }

                addAppButton.setVisibility(View.INVISIBLE);
                trendingAppsList.setVisibility(View.GONE);
                trendingAppsAdapter.clearAdapter();
                LoadersUtils.showCenterLoader(MainActivity.this);
            } else {
                if (fragment != null) {
                    getSupportFragmentManager().popBackStack(Constants.TAG_NOTIFICATION_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                onNetworkAvailable();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_app:
                startSelectAppFragment();

                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                break;

            case R.id.reload:
                v.setVisibility(View.GONE);
                trendingAppsAdapter.resetAdapter();
                addAppButton.setVisibility(View.VISIBLE);
                trendingAppsList.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void startSelectAppFragment() {
        if (LoginProviderFactory.get(this).isUserLoggedIn()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                    .add(R.id.container, new SelectAppFragment(), Constants.TAG_SELECT_APP_FRAGMENT)
                    .addToBackStack(Constants.TAG_SELECT_APP_FRAGMENT)
                    .commit();

            getSupportFragmentManager().executePendingTransactions();
        } else {
            FacebookUtils.showLoginFragment(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (LoginProviderFactory.get(this).isUserLoggedIn()) {
            menu.findItem(R.id.action_login).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
        } else {
            menu.findItem(R.id.action_login).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            menu.findItem(R.id.action_search).setVisible(false);
        } else {
            menu.findItem(R.id.action_search).setVisible(true);
        }

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                AppHuntApiClient.getClient().searchApps(s, SharedPreferencesHelper.getStringPreference(MainActivity.this, Constants.KEY_USER_ID), 1, Constants.SEARCH_RESULT_COUNT,
                        Constants.PLATFORM, new Callback<AppsList>() {
                            @Override
                            public void success(AppsList appsList, Response response) {
                                trendingAppsAdapter.showSearchResult(appsList.getApps());
                            }
                        });

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
                    trendingAppsAdapter.clearSearch();
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
            case R.id.action_login:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                        getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals(Constants.TAG_LOGIN_FRAGMENT))
                    break;

                FacebookUtils.showLoginFragment(this);
                break;

            case R.id.action_logout:
                LoginProviderFactory.get(this).logout();
                FlurryAgent.logEvent(TrackingEvents.UserLoggedOut);
                supportInvalidateOptionsMenu();
                break;

            case R.id.action_settings:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                        getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals(Constants.TAG_SETTINGS_FRAGMENT))
                    break;
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new SettingsFragment(), Constants.TAG_SETTINGS_FRAGMENT)
                        .addToBackStack(Constants.TAG_SETTINGS_FRAGMENT)
                        .commit();
                break;

            case R.id.action_share:
                if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                            .setName("AppHunt")
                            .setPicture("https://launchrock-assets.s3.amazonaws.com/logo-files/LWPRHM35_1421410706452.png?_=4")
                            .setLink(Constants.GOOGLE_PLAY_APP_URL).build();
                    shareDialog.present();
                    FlurryAgent.logEvent(TrackingEvents.UserSharedAppHuntWithFacebook);
                } else {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/html");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.share_text)));
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    FlurryAgent.logEvent(TrackingEvents.UserSharedAppHuntWithoutFacebook);
                }
                break;

            case R.id.action_suggest:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                        getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals(Constants.TAG_SUGGEST_FRAGMENT))
                    break;
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new SuggestFragment(), Constants.TAG_SUGGEST_FRAGMENT)
                        .addToBackStack(Constants.TAG_SUGGEST_FRAGMENT)
                        .commit();
                break;

            case android.R.id.home:
                AppDetailsFragment fragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_APP_DETAILS_FRAGMENT);
                if (fragment != null && fragment.isVisible() && fragment.isCommentsBoxOpened()) {
                    fragment.showDetails();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(Constants.TAG_LOGIN_FRAGMENT);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        endOfList = (firstVisibleItem + visibleItemCount) == totalItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (ConnectivityUtils.isNetworkAvailable(this)) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                Animation slideInBottom = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom);

                addAppButton.startAnimation(slideInBottom);
                addAppButton.setVisibility(View.VISIBLE);

                if (endOfList && trendingAppsAdapter.couldLoadMoreApps()) {
                    FlurryAgent.logEvent(TrackingEvents.UserScrolledDownAppList);
                    trendingAppsAdapter.getAppsForNextDate();
                }
            } else {
                Animation slideOutBottom = AnimationUtils.loadAnimation(this, R.anim.abc_slide_out_bottom);
                addAppButton.startAnimation(slideOutBottom);
                addAppButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onAppSelected(ApplicationInfo data) {
        String curFragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

        if (!curFragmentTag.equals(Constants.TAG_SAVE_APP_FRAGMENT)) {
            Bundle extras = new Bundle();
            extras.putParcelable(Constants.KEY_DATA, data);

            SaveAppFragment saveAppFragment = new SaveAppFragment();
            saveAppFragment.setArguments(extras);

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .add(R.id.container, saveAppFragment, Constants.TAG_SAVE_APP_FRAGMENT)
                    .addToBackStack(Constants.TAG_SAVE_APP_FRAGMENT)
                    .commit();
        }
    }

    public void setOnBackBlocked(boolean isBlocked) {
        if (isBlocked) {
            ActionBarUtils.getInstance().hideActionBar(this);
        } else {
            ActionBarUtils.getInstance().showActionBar(this);
        }
        this.isBlocked = isBlocked;
    }

    @Override
    public void onUserLogin() {
        setOnBackBlocked(false);
        LoadersUtils.hideBottomLoader(this);
        trendingAppsAdapter.resetAdapter();

        AppDetailsFragment fragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_APP_DETAILS_FRAGMENT);
        if (fragment != null && fragment.isVisible()) {
            fragment.loadData();
        }
    }

    @Override
    public void onUserLogout() {
        setOnBackBlocked(false);
        LoadersUtils.hideBottomLoader(this);
        trendingAppsAdapter.resetAdapter();

        AppDetailsFragment fragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_APP_DETAILS_FRAGMENT);
        if (fragment != null && fragment.isVisible()) {
            fragment.loadData();
        }
    }

    @Override
    public void onNetworkAvailable() {
        if (trendingAppsAdapter.getCount() == 0) {
            LoadersUtils.hideCenterLoader(this);
            reloadButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAppVote(int position) {
        trendingAppsAdapter.resetAdapter(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppSpice.onResume(this);

        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void showStartFragments(Intent intent) {
        if (isStartedFromShareIntent(intent)) {
            startSelectAppFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AppSpice.onPause(this);
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onBackPressed() {
        if (!isBlocked) {
            AppDetailsFragment fragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_APP_DETAILS_FRAGMENT);
            if (fragment != null && fragment.isVisible() && fragment.isCommentsBoxOpened()) {
                fragment.showDetails();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppSpice.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppSpice.onStop(this);
    }

    public void updateNotificationIdIfNeeded() {
        final String userId = SharedPreferencesHelper.getStringPreference(this, Constants.KEY_USER_ID);
        String notificationId = SharedPreferencesHelper.getStringPreference(this, Constants.KEY_NOTIFICATION_ID);
        if (!TextUtils.isEmpty(userId) && TextUtils.isEmpty(notificationId)) {
            FruityGcmClient.start(this, Constants.GCM_SENDER_ID, new FruityGcmListener() {

                @Override
                public void onPlayServiceNotAvailable(boolean b) {
                }

                @Override
                public void onDeliverRegistrationId(final String regId, boolean b) {
                    User user = new User();
                    user.setNotificationId(regId);
                    AppHuntApiClient.getClient().updateUser(userId, user, new Callback<User>() {
                        @Override
                        public void success(User user, Response response) {
                            SharedPreferencesHelper.setPreference(MainActivity.this, Constants.KEY_NOTIFICATION_ID, regId);
                        }
                    });
                }

                @Override
                public void onRegisterFailed() {
                }
            });
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onRateDialogVariableReady(RateDialogVariable rateDialogVariable) {
        SmartRate.setRateDialogVariable(rateDialogVariable);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onAppSpiceError(AppSpiceError error) {
        SmartRate.onError();
    }
}
